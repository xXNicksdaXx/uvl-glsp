/*
 * Copyright Â© 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.service;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Injectable service that maintains a Server-Sent Events (SSE) connection for the BP GLSP server.
 */
public interface BPServerSentEventsService {

  /** Starts the SSE listener if it is not already running. */
  void start();

  /** Stops the SSE listener and releases background resources. */
  void stop();

  /**
   * @return true if the SSE listener is currently running.
   */
  boolean isRunning();

  /**
   * @return the configured endpoint URI that is used for the SSE stream.
   */
  URI getEndpoint();

  /**
   * Registers a listener that receives decoded SSE payloads from the data field.
   *
   * @param listener callback for incoming data payloads
   */
  void addDataListener(Consumer<String> listener);

  /**
   * Unregisters a previously registered listener.
   *
   * @param listener callback to remove
   */
  void removeDataListener(Consumer<String> listener);

  /**
   * Enables higher-frequency health polling for a short time window.
   *
   * <p>Use this when GLSP expects the SSE API to become available soon after startup.
   */
  void triggerHighFrequencyPolling();
}
