/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.notation;

public interface EdgeNotation {

    String getId();
    void setId(String id);

    String getSourceId();
    void setSourceId(String sourceId);

    String getTargetId();
    void setTargetId(String targetId);
}
