/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPModelState;
import de.tu_dresden.inf.st.uvl.bp.glsp.utils.BTypeUtil;
import de.tu_dresden.inf.st.uvl.glsp.actions.HighlightElementAction;

import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.server.actions.ActionDispatcher;

/** Parses SSE payloads from the FMBP event listener and dispatches matching highlight actions. */
@Singleton
public class FMBPHighlightActionDispatchService {

  private static final Logger LOGGER =
      LogManager.getLogger(FMBPHighlightActionDispatchService.class.getName());

  private final Gson gson = new Gson();

  @Inject protected ServerSentEventsService serverSentEventsService;

  @Inject protected ActionDispatcher actionDispatcher;

  @Inject protected BPModelState modelState;

  @Inject
  protected void registerDataListener() {
    serverSentEventsService.addDataListener(this::dispatchHighlightAction);
  }

  protected void dispatchHighlightAction(final String payload) {
    Map<?, ?> parsedPayload = parsePayload(payload);
    Optional<String> eventName = parseEventName(parsedPayload);
    if (eventName.isEmpty()) {
      return;
    }

    Optional<Path> sourcePath = parseSourcePath(parsedPayload);
    if (sourcePath.isPresent()) {
      Map<String, String> options = modelState.getClientOptions();
      String sourceUri = options.get("sourceUri");
      Path expectedSourcePath = Path.of(URI.create(sourceUri));
      if (!sourcePath.get().equals(expectedSourcePath)) {
        return;
      }
    }

    Set<String> matchingFeatureIds = resolveFeatureIdsByEventName(eventName.get());
    if (matchingFeatureIds.isEmpty()) {
      return;
    }

    actionDispatcher.dispatch(new HighlightElementAction(List.copyOf(matchingFeatureIds), true));
  }

  protected Map<?, ?> parsePayload(final String payload) {
    try {
      Object root = gson.fromJson(payload, Object.class);
      if (root instanceof Map<?, ?> map) {
        return map;
      }
    } catch (RuntimeException runtimeException) {
      LOGGER.debug(
          "Unable to parse SSE payload as JSON, falling back to raw payload.", runtimeException);
    }
    return Map.of();
  }

  protected Optional<String> parseEventName(final Map<?, ?> payload) {
    if (payload.containsKey("event")) {
      Object event = payload.get("event");
      if (event instanceof String eventName && !eventName.isBlank()) {
        return Optional.of(eventName.trim());
      }
    }
    return Optional.empty();
  }

  protected Optional<Path> parseSourcePath(final Map<?, ?> payload) {
    if (payload.containsKey("source")) {
      Object source = payload.get("source");
      if (source instanceof String sourceName) {
        return Optional.of(Path.of(sourceName.trim()));
      }
    }
    return Optional.empty();
  }

  protected Set<String> resolveFeatureIdsByEventName(final String eventName) {
    if (modelState.getFeatureModel() == null || eventName == null || eventName.isBlank()) {
      return Set.of();
    }

    Set<String> ids = new LinkedHashSet<>();
    BTypeUtil.getAllBThreadsWithBEvent(modelState.getFeatureModel(), eventName).stream()
        .map(feature -> modelState.getIndex().getIdFor(feature))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(ids::add);

    return ids;
  }
}
