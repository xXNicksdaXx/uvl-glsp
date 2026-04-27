/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.bp.glsp.gmodel.BPAdditionalElementsFactory;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPModelState;
import de.tu_dresden.inf.st.uvl.metamodel.model.Attribute;
import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.server.actions.Action;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.features.core.model.ModelSubmissionHandler;

@Singleton
public class FMBPContextUpdateService {

  private static final Logger LOGGER =
      LogManager.getLogger(FMBPContextUpdateService.class.getName());

  private static final String CONTEXT_UPDATE_TYPE = "context_update";

  @Inject protected ActionDispatcher actionDispatcher;

  @Inject protected ModelSubmissionHandler submissionHandler;

  @Inject protected BPModelState modelState;

  @Inject protected BPAdditionalElementsFactory additionalElementsFactory;

  @Inject protected ServerSentEventsService serverSentEventsService;

  @Inject
  protected void registerDataListener() {
    serverSentEventsService.addDataListener(this::updateContextEnv, CONTEXT_UPDATE_TYPE);
  }

  protected void updateContextEnv(final ParsedServerSentEvent event) {
    Optional<Path> sourcePath = parseSourcePath(event);
    if (sourcePath.isPresent()) {
      Map<String, String> options = modelState.getClientOptions();
      if (options == null) {
        return;
      }

      // check if source URI from FMBP matches the one of GLSP
      String sourceUri = options.get("sourceUri");
      if (sourceUri == null || sourceUri.isBlank()) {
        return;
      }
      Path expectedSourcePath = Path.of(URI.create(sourceUri));
      if (!sourcePath.get().equals(expectedSourcePath)) {
        return;
      }
    }

    if (modelState.getFeatureModel() == null || modelState.getFeatureModel().getEnv() == null) {
      return;
    }

    Map<String, ?> data = event.data();
    if (data.isEmpty()) {
      return;
    }

    Feature envFeature = modelState.getFeatureModel().getEnv();

    boolean hasChanges = false;
    for (Map.Entry<String, ?> entry : data.entrySet()) {
      String attributeName = entry.getKey();
      if (envFeature.getAttributes().containsKey(attributeName)) {
        Attribute attribute = envFeature.getAttributes().get(attributeName);
        if (!Objects.equals(attribute.getValue(), entry.getValue())) {
          attribute.setValue(entry.getValue());
          hasChanges = true;
        }
      }
    }

    if (hasChanges) {
      List<Action> actions = submitModel(envFeature);
      actionDispatcher.dispatchAll(actions);
    }
  }

  protected List<Action> submitModel(Feature env) {
    synchronized (submissionHandler.getModelLock()) {
      modelState.updateIndex();
      GNode updatedEnvNode = additionalElementsFactory.createEnv(env);

      GModelRoot model = modelState.getRoot();
      if (model != null) {
        GModelRoot updatedModel = replaceEnvGNode(model, updatedEnvNode);
        modelState.updateRoot(updatedModel);
        return submissionHandler.submitModel();
      }
    }
    return List.of();
  }

  protected GModelRoot replaceEnvGNode(GModelRoot model, GNode envNode) {
    for (GModelElement element : model.getChildren()) {
      if (element.getId().equals(envNode.getId())) {
        model.getChildren().remove(element);
        model.getChildren().add(envNode);
        return model;
      }
    }
    return model;
  }

  protected Optional<Path> parseSourcePath(final ParsedServerSentEvent event) {
    return event.source().map(Path::of);
  }
}
