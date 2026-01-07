/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.notation;

import de.vill.model.Feature;

import java.util.HashMap;
import java.util.Map;

import static de.tu_dresden.inf.st.uvl.glsp.utils.FeatureUtil.getFeatureId;

public class NotationData {
    private Map<String, ElementNotation> elements = new HashMap<>();
    private Map<String, EdgeNotation> edges = new HashMap<>();

    public Map<String, ElementNotation> getElements() {
        return elements;
    }

    public void setElements(Map<String, ElementNotation> elements) {
        this.elements = elements;
    }

    public ElementNotation getElementNotation(String elementId) {
        return elements.get(elementId);
    }

    public ElementNotation getElementNotation(Feature feature) {
        return elements.get(getFeatureId(feature));
    }

    public void setElementNotation(String elementId, ElementNotation notation) {
        elements.put(elementId, notation);
    }

    public Map<String, EdgeNotation> getEdges() {
        return edges;
    }

    public void setEdges(Map<String, EdgeNotation> edges) {
        this.edges = edges;
    }

    public EdgeNotation getEdgeNotation(String edgeId) {
        return edges.get(edgeId);
    }

    public EdgeNotation getEdgeNotation(Feature source, Feature target) {
        String edgeId = getFeatureId(source) + "to" + getFeatureId(target);
        return edges.get(edgeId);
    }

    public void setEdgeNotation(String edgeId, EdgeNotation notation) {
        edges.put(edgeId, notation);
    }
}
