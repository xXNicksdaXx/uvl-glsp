/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import de.vill.model.FeatureModel;
import org.eclipse.glsp.server.model.GModelState;

public interface UVLModelState extends GModelState {

    FeatureModel getUVLModel();

    void setUVLModel(FeatureModel model);
}
