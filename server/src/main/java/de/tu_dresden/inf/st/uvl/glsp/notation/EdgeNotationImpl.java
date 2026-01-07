/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.notation;

import org.eclipse.glsp.graph.GPoint;

import java.util.ArrayList;
import java.util.List;

public class EdgeNotationImpl implements EdgeNotation {
    private String sourceId;
    private String targetId;
    private List<GPoint> bendPoints;

    public EdgeNotationImpl() {
    }

    public EdgeNotationImpl(String sourceId, String targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.bendPoints = new ArrayList<>();
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<GPoint> getBendPoints() {
        return bendPoints;
    }

    public void setBendPoints(List<GPoint> bendPoints) {
        this.bendPoints = bendPoints;
    }
}
