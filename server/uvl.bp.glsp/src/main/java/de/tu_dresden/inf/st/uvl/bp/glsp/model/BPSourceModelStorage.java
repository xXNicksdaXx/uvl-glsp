/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.model;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.bp.glsp.service.ServerSentEventsService;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLSourceModelStorage;
import de.tu_dresden.inf.st.uvl.metamodel.main.ModelType;
import de.tu_dresden.inf.st.uvl.metamodel.main.UVLModelFactory;
import de.tu_dresden.inf.st.uvl.metamodel.model.BPFeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;

public class BPSourceModelStorage extends UVLSourceModelStorage {

  @Inject protected ServerSentEventsService serverSentEventsService;

  @Override
  public void loadSourceModel(final RequestModelAction action) {
    super.loadSourceModel(action);
    if (!serverSentEventsService.isRunning()) {
      serverSentEventsService.start();
    }
  }

  @Override
  protected FeatureModel parseFeatureModel(final String content) {
    boolean isEmpty = content.trim().isEmpty();
    if (isEmpty) {
      return new BPFeatureModel();
    }

    UVLModelFactory uvlModelFactory = new UVLModelFactory();
    return uvlModelFactory.parse(content, ModelType.BP);
  }
}
