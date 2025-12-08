package de.tu_dresden.inf.st.uvl.glsp.utils;

import de.vill.model.Feature;

public class FeatureUtil {

    public static String getFeatureId(Feature feature) {
        String fullReference = feature.getFullReference();
        return fullReference.substring(fullReference.lastIndexOf("_") + 1);
    }
}
