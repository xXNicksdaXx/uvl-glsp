/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp;

import org.eclipse.glsp.graph.DefaultTypes;

/**
 * Defines string constants for all element types used in UVL diagrams.
 * <p>
 * For corresponding client-side types, see <code>uvl-model-types.ts</code>.
 * <p>
 * This class is not intended to be instantiated.
 */
public final class UVLModelTypes {

    public static final String FEATURE = DefaultTypes.NODE + ":feature";
    public static final String CONSTRAINT = DefaultTypes.NODE + ":constraint";

    public static final String B_THREAD = DefaultTypes.LABEL + ":b-thread";

    public static final String MANDATORY = DefaultTypes.EDGE + ":mandatory";
    public static final String OPTIONAL = DefaultTypes.EDGE + ":optional";
    public static final String ALTERNATIVE = DefaultTypes.EDGE + ":alternative";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UVLModelTypes() {
        // Prevent instantiation
    }
}
