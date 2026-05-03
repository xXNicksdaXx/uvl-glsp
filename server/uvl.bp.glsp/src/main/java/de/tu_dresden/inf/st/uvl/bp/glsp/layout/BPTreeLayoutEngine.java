/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.layout;

import de.tu_dresden.inf.st.uvl.glsp.layout.UVLTreeLayoutEngine;
import de.tu_dresden.inf.st.uvl.glsp.layout.WalkersNode;
import de.tu_dresden.inf.st.uvl.metamodel.model.BPFeatureModel;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.GShapeElement;

public class BPTreeLayoutEngine extends UVLTreeLayoutEngine {

  @Override
  protected List<GShapeElement> buildPostProcessPipeline(final WalkersNode root) {
    List<GShapeElement> pipeline = new ArrayList<>();
    modelState.getIndex().getGModelElement("constraint_box", GNode.class).ifPresent(pipeline::add);

    if (modelState.getFeatureModel() instanceof BPFeatureModel bpFeatureModel) {
      modelState
          .getIndex()
          .getGModelElement(bpFeatureModel.getConfig(), GNode.class)
          .ifPresent(pipeline::add);
      modelState
          .getIndex()
          .getGModelElement(bpFeatureModel.getEnv(), GNode.class)
          .ifPresent(pipeline::add);
    }

    return pipeline;
  }
}
