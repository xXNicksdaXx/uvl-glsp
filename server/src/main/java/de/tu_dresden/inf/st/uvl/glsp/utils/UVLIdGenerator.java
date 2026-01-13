/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.utils;

import java.util.Map;
import java.util.UUID;

public class UVLIdGenerator {
    public String getOrCreateId(final Object element, final Map<Object, String> idMap) {
        if (idMap.containsKey(element)) {
            return idMap.get(element);
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
