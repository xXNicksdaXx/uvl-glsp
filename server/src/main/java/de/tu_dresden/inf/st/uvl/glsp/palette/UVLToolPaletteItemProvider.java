/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.palette;

import java.util.List;
import java.util.Map;

import org.eclipse.glsp.server.actions.TriggerEdgeCreationAction;
import org.eclipse.glsp.server.actions.TriggerNodeCreationAction;
import org.eclipse.glsp.server.features.toolpalette.PaletteItem;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;

import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;

public class UVLToolPaletteItemProvider implements ToolPaletteItemProvider {

    @Override
    public List<PaletteItem> getItems(Map<String, String> args) {
        return List.of(
                elements(),
                relations(),
                additionals()
        );
    }

    private PaletteItem elements() {
        List<PaletteItem> elements = List.of(
                node(UVLModelTypes.FEATURE, "Feature"),
                node(UVLModelTypes.CONSTRAINT, "Constraint"));
        return PaletteItem.createPaletteGroup("elements", "Elements", elements, "symbol-property", "A");
    }

    private PaletteItem relations() {
        List<PaletteItem> relations = List.of(
                edge(UVLModelTypes.MANDATORY, "Mandatory"),
                edge(UVLModelTypes.OPTIONAL, "Optional"),
                edge(UVLModelTypes.ALTERNATIVE, "Alternative"));
        return PaletteItem.createPaletteGroup("relations", "Relations", relations, "symbol-property", "C");
    }

    private PaletteItem additionals() {
        List<PaletteItem> features = List.of(
                node(UVLModelTypes.B_THREAD, "B-Thread"));
        return PaletteItem.createPaletteGroup("additionals", "Additionals", features, "symbol-property", "C");
    }

    /**
     * Creates a palette item for node creation.
     * <p>
     * The returned palette item, when selected, triggers the creation of a node of
     * the specified type in the diagram.
     *
     * @param elementTypeId The type ID of the node element to create (see {@link UVLModelTypes})
     * @param label         The label to display for this palette item
     *
     * @return A {@link PaletteItem} that triggers node creation when selected
     */
    private PaletteItem node(String elementTypeId, String label) {
        return new PaletteItem(elementTypeId, label, new TriggerNodeCreationAction(elementTypeId));
    }

    /**
     * Creates a palette item for edge creation.
     * <p>
     * The returned palette item, when selected, triggers the creation of an edge of
     * the specified type in the diagram.
     *
     * @param elementTypeId The type ID of the edge element to create (see {@link UVLModelTypes})
     * @param label         The label to display for this palette item
     *
     * @return A {@link PaletteItem} that triggers edge creation when selected
     */
    private PaletteItem edge(String elementTypeId, String label) {
        return new PaletteItem(elementTypeId, label, new TriggerEdgeCreationAction(elementTypeId));
    }

}
