/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.notation;

import org.eclipse.glsp.graph.GPoint;

import java.util.List;

public interface EdgeNotation {

    String getSourceId();
    void setSourceId(String sourceId);

    String getTargetId();
    void setTargetId(String targetId);

    List<GPoint> getBendPoints();
    void setBendPoints(List<GPoint> bendPoints);
}
