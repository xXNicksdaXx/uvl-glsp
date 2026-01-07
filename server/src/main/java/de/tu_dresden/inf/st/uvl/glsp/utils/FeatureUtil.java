/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.utils;

import de.vill.model.Feature;

public class FeatureUtil {

    public static String getFeatureId(Feature feature) {
        return feature.getFeatureName();
    }
}
