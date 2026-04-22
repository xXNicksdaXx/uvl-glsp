/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Parsed SSE payload delivered to data listeners. */
public record ParsedServerSentEvent(
    String rawPayload,
    Map<String, Object> payload,
    Optional<String> type,
    Optional<String> source,
    Optional<Instant> timestamp,
    Map<String, Object> data) {

  public static ParsedServerSentEvent from(final String rawPayload, final Map<?, ?> payload) {
    Map<String, Object> normalizedPayload = normalizeMap(payload);
    Optional<String> type = extractString(normalizedPayload, "type");
    Optional<String> source = extractString(normalizedPayload, "source");
    Optional<Instant> timestamp = extractTimestamp(normalizedPayload);
    Map<String, Object> data = extractMap(normalizedPayload);
    return new ParsedServerSentEvent(rawPayload, normalizedPayload, type, source, timestamp, data);
  }

  public boolean hasType(final String... eventTypes) {
    if (eventTypes == null || eventTypes.length == 0) {
      return true;
    }

    return type.map(
            current -> {
              for (String eventType : eventTypes) {
                if (eventType != null && !eventType.isBlank() && current.equals(eventType.trim())) {
                  return true;
                }
              }
              return false;
            })
        .orElse(false);
  }

  private static Map<String, Object> extractMap(final Map<String, Object> payload) {
    Object value = payload.get("data");
    if (value instanceof Map<?, ?> rawMap) {
      return normalizeMap(rawMap);
    }
    return Map.of();
  }

  private static Optional<String> extractString(
      final Map<String, Object> payload, final String key) {
    Object value = payload.get(key);
    if (value instanceof String stringValue && !stringValue.isBlank()) {
      return Optional.of(stringValue.trim());
    }
    return Optional.empty();
  }

  private static Optional<Instant> extractTimestamp(final Map<String, Object> payload) {
    Object value = payload.get("timestamp");
    if (value instanceof String stringValue && !stringValue.isBlank()) {
      try {
        return Optional.of(OffsetDateTime.parse(stringValue.trim()).toInstant());
      } catch (RuntimeException ignored) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  private static Map<String, Object> normalizeMap(final Map<?, ?> source) {
    Map<String, Object> normalized = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : source.entrySet()) {
      if (entry.getKey() instanceof String key) {
        normalized.put(key, entry.getValue());
      }
    }
    return normalized;
  }
}
