/*
 * Copyright Â© 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** SSE implementation that connects to a FMBP endpoint and automatically reconnects. */
@Singleton
public class FMBPEventListenerService implements BPServerSentEventsService {

  private static final Logger LOGGER = LogManager.getLogger(FMBPEventListenerService.class.getName());
  private static final URI DEFAULT_ENDPOINT = URI.create("http://localhost:8099");
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration DEFAULT_HEALTH_POLL_INTERVAL = Duration.ofSeconds(10);
  private static final Duration HIGH_FREQUENCY_HEALTH_POLL_INTERVAL = Duration.ofSeconds(2);
  private static final Duration HIGH_FREQUENCY_POLL_WINDOW = Duration.ofMinutes(2);
  private static final Duration HEALTH_REQUEST_TIMEOUT = Duration.ofSeconds(2);
  private static final String ENDPOINT_PROPERTY = "uvl.bp.sse.endpoint";

  private final HttpClient httpClient;
  private final URI endpoint;
  private final ExecutorService executor;
  private final AtomicBoolean running;
  private final AtomicLong highFrequencyPollingUntilEpochMillis;
  private final List<Consumer<String>> listeners;

  private volatile CompletableFuture<?> streamTask;

  @Inject
  public FMBPEventListenerService() {
    this.httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
    this.endpoint = URI.create(System.getProperty(ENDPOINT_PROPERTY, DEFAULT_ENDPOINT.toString()));
    this.executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "bp-sse-listener"));
    this.running = new AtomicBoolean(false);
    this.highFrequencyPollingUntilEpochMillis = new AtomicLong(0);
    this.listeners = new CopyOnWriteArrayList<>();
    start();
  }

  @Override
  public void start() {
    if (!running.compareAndSet(false, true)) {
      return;
    }
    streamTask = CompletableFuture.runAsync(this::runStreamLoop, executor);
    LOGGER.info("BP SSE service started. Endpoint: {}", endpoint);
  }

  @Override
  public void stop() {
    if (!running.compareAndSet(true, false)) {
      return;
    }

    CompletableFuture<?> task = streamTask;
    if (task != null) {
      task.cancel(true);
    }

    executor.shutdownNow();
    try {
      if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
        LOGGER.warn("Timed out while stopping BP SSE listener thread.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    LOGGER.info("BP SSE service stopped.");
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public URI getEndpoint() {
    return endpoint;
  }

  @Override
  public void addDataListener(final Consumer<String> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeDataListener(final Consumer<String> listener) {
    listeners.remove(listener);
  }

  @Override
  public void triggerHighFrequencyPolling() {
    long now = System.currentTimeMillis();
    long boostedUntil = now + HIGH_FREQUENCY_POLL_WINDOW.toMillis();
    highFrequencyPollingUntilEpochMillis.updateAndGet(previous -> Math.max(previous, boostedUntil));
    LOGGER.info(
        "Enabled high-frequency health polling ({}s) for {}s.",
        HIGH_FREQUENCY_HEALTH_POLL_INTERVAL.toSeconds(),
        HIGH_FREQUENCY_POLL_WINDOW.toSeconds());
  }

  protected void runStreamLoop() {
    while (running.get()) {
      waitForHealthyEndpoint();
      if (!running.get()) {
        return;
      }

      HttpRequest request =
          HttpRequest.newBuilder(resolveEventsEndpoint())
              .header("Accept", "text/event-stream")
              .timeout(CONNECT_TIMEOUT)
              .GET()
              .build();

      try {
        HttpResponse<java.util.stream.Stream<String>> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofLines());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
          LOGGER.warn(
              "SSE endpoint returned status " + response.statusCode() + ". Reconnecting...");
          sleepBeforeReconnect();
          continue;
        }

        consumeSseLines(response.body());
      } catch (IOException e) {
        LOGGER.warn("SSE connection failed. Reconnecting...", e);
        sleepBeforeReconnect();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        if (running.get()) {
          LOGGER.warn("SSE listener interrupted unexpectedly.", e);
        }
        return;
      } catch (UncheckedIOException e) {
        if (isExpectedDisconnect(e)) {
          LOGGER.info("SSE connection was lost. Reconnecting...");
        } else {
          LOGGER.warn("SSE stream I/O error. Reconnecting...", e);
        }
        sleepBeforeReconnect();
      } catch (RuntimeException e) {
        if (isExpectedDisconnect(e)) {
          LOGGER.info("SSE connection was lost. Reconnecting...");
        } else {
          LOGGER.warn("Unexpected SSE listener error. Reconnecting...", e);
        }
        sleepBeforeReconnect();
      }
    }
  }

  protected boolean isExpectedDisconnect(final Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof java.net.SocketException) {
        return true;
      }

      String message = current.getMessage();
      if (message != null) {
        String lowered = message.toLowerCase();
        if (lowered.contains("connection reset")
            || lowered.contains("closed")
            || lowered.contains("chunked transfer encoding")) {
          return true;
        }
      }

      current = current.getCause();
    }
    return false;
  }

  protected void waitForHealthyEndpoint() {
    while (running.get()) {
      HttpRequest request =
          HttpRequest.newBuilder(resolveHealthEndpoint())
              .timeout(HEALTH_REQUEST_TIMEOUT)
              .GET()
              .build();

      try {
        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
          return;
        }

        LOGGER.info("Flask health endpoint returned {}, retrying...", response.statusCode());
      } catch (IOException e) {
        LOGGER.info("Flask health endpoint is not reachable yet, retrying...");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      sleepBeforeReconnect();
    }
  }

  protected URI resolveHealthEndpoint() {
    return endpoint.resolve("/health");
  }

  protected URI resolveEventsEndpoint() {
    return endpoint.resolve("/events");
  }

  protected void consumeSseLines(final java.util.stream.Stream<String> lines) {
    StringBuilder dataBuffer = new StringBuilder();
    try (lines) {
      Iterator<String> iterator = lines.iterator();
      while (running.get() && iterator.hasNext()) {
        String line = iterator.next();
        if (line.isEmpty()) {
          dispatchBufferedData(dataBuffer);
          continue;
        }

        if (line.startsWith("data:")) {
          String value = line.length() > 5 ? line.substring(5).trim() : "";
          if (!value.isEmpty()) {
            if (!dataBuffer.isEmpty()) {
              dataBuffer.append('\n');
            }
            dataBuffer.append(value);
          }
        }
      }
      dispatchBufferedData(dataBuffer);
    }
  }

  protected void dispatchBufferedData(final StringBuilder dataBuffer) {
    if (dataBuffer.isEmpty()) {
      return;
    }

    String payload = dataBuffer.toString();
    dataBuffer.setLength(0);

    LOGGER.info("SSE payload received: {}", payload);

    for (Consumer<String> listener : listeners) {
      try {
        listener.accept(payload);
      } catch (RuntimeException e) {
        LOGGER.warn("SSE listener callback threw an exception.", e);
      }
    }
  }

  protected void sleepBeforeReconnect() {
    try {
      Thread.sleep(resolveCurrentPollInterval().toMillis());
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    }
  }

  protected Duration resolveCurrentPollInterval() {
    if (System.currentTimeMillis() < highFrequencyPollingUntilEpochMillis.get()) {
      return HIGH_FREQUENCY_HEALTH_POLL_INTERVAL;
    }
    return DEFAULT_HEALTH_POLL_INTERVAL;
  }
}
