/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.utils;

import de.tu_dresden.inf.st.uvl.glsp.notation.ElementNotation;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;

public class NotationUtil {

    public static GNodeBuilder applyNotationData(final ElementNotation notationData, final GNodeBuilder builder) {
        if (notationData != null) {
            builder.size(notationData.getWidth(), notationData.getHeight());
            builder.position(notationData.getX(), notationData.getY());
        }
        return builder;
    }

}
