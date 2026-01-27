/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.vill.main.UVLModelFactory;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.graph.*;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.server.actions.SaveModelAction;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;
import org.eclipse.glsp.server.gmodel.GModelStorage;
import org.eclipse.glsp.server.layout.LayoutEngine;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class UVLSourceModelStorage extends GModelStorage implements SourceModelStorage {

    protected static Logger LOGGER = LogManager.getLogger(UVLSourceModelStorage.class.getSimpleName());

    @Inject
    protected UVLModelState modelState;

    @Inject
    protected LayoutEngine layoutEngine;

    @Override
    public void loadSourceModel(RequestModelAction action) {
        File featureModelFile = convertToFile(action.getOptions());
        loadFeatureModel(featureModelFile);

        String filePath = featureModelFile.getAbsolutePath();
        File notationFile = getNotationFile(filePath);
        loadGModel(notationFile);

        if (requiresLayoutOperation()) {
            layoutEngine.layout(Optional.empty());
        }
    }

    protected void loadFeatureModel(final File featureModelFile) {
        String filePath = featureModelFile.getAbsolutePath();

        try {
            String content = Files.readString(Paths.get(filePath));
            boolean isEmpty = content.trim().isEmpty();
            if (isEmpty) {
                modelState.setFeatureModel(new FeatureModel());
                return;
            }

            UVLModelFactory uvlModelFactory = new UVLModelFactory();
            FeatureModel featureModel = uvlModelFactory.parse(content);
            modelState.setFeatureModel(featureModel);
        } catch (IndexOutOfBoundsException e) {
            modelState.setFeatureModel(new FeatureModel());
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("Could not load FeatureModel from file: " + featureModelFile.toURI(), e);
        }
    }

    protected void loadGModel(final File notationFile) {
        try (Reader reader = new InputStreamReader(new FileInputStream(notationFile), StandardCharsets.UTF_8)) {
            GGraph root = gson.fromJson(reader, GGraph.class);
            if (root == null) {
                boolean isEmpty = notationFile.length() == 0;
                if (isEmpty) {
                    GModelRoot newRoot = createNewEmptyRoot(modelState);
                    modelState.updateRoot(newRoot);
                    resolveUnreferencedElements(modelState.getRoot());
                    return;
                }
                throw new IOException("Could not deserialize GModel file contents of: " + notationFile.toURI());
            }
            modelState.updateRoot(root);
            resolveUnreferencedElements(modelState.getRoot());
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("Could not load GModel from file: " + notationFile.toURI(), e);
        }
    }

    @Override
    protected GModelRoot createNewEmptyRoot(final GModelState modelState) {
        GModelRoot root = GraphFactory.eINSTANCE.createGGraph();
        root.setId(ClientOptionsUtil.getSourceUri(modelState.getClientOptions()).orElse("root"));
        root.setRevision(modelState.getRoot() != null ? root.getRevision() : -1);
        root.setType(DefaultTypes.GRAPH);
        return root;
    }

    protected void resolveUnreferencedElements(GModelRoot root) {
        Set<String> existingElements = modelState.getIndex().allFeatureModelIds();
        for (String featureModelId : existingElements) {
            boolean existsInGModel = root.getChildren().stream()
                    .anyMatch(element -> featureModelId.equals(element.getId()));
            if (!existsInGModel) {
                addGModelElement(featureModelId, root);
            }
        }
    }

    private void addGModelElement(String id, GModelRoot root) {
        Optional<Object> element = modelState.getIndex().getUVLObject(id);
        if (element.isPresent()) {
            Object uvlObject = element.get();
            if (uvlObject instanceof Feature) {
                GNode node = new GNodeBuilder(DefaultTypes.NODE)
                        .id(id)
                        .size(64, 32)
                        .position(0, 0)
                        .build();
                root.getChildren().add(node);
            } else if (uvlObject instanceof Group group) {
                for (Feature target : group.getFeatures()) {
                    String sourceId = modelState.getIndex().getIdFor(group.getParentFeature());
                    String targetId = modelState.getIndex().getIdFor(target);
                    GEdge edge = new GEdgeBuilder(DefaultTypes.EDGE)
                            .id(id + "_" + targetId)
                            .sourceId(sourceId)
                            .targetId(targetId)
                            .build();
                    root.getChildren().add(edge);
                }
            }
        }
    }

    @Override
    public void saveSourceModel(SaveModelAction action) {
        File featureModelFile = convertToFile(action);
        saveFeatureModel(featureModelFile);

        String filePath = featureModelFile.getAbsolutePath();
        File notationFile = getNotationFile(filePath);
        saveGModel(notationFile);
    }

    protected void saveFeatureModel(final File featureModelFile) {
        final FeatureModel featureModel = modelState.getFeatureModel();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(featureModelFile), StandardCharsets.UTF_8)) {
            String uvlModel = featureModel.toString();
            writer.write(uvlModel);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("An error occurred during save process of the FeatureModel file.", e);
        }

    }

    protected void saveGModel(final File notationFile) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(notationFile), StandardCharsets.UTF_8)) {
            GGraph root = (GGraph) modelState.getRoot();
            GGraph reducedGraph = reduceGGraph(root);
            gson.toJson(reducedGraph, GGraph.class, writer);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("An error occurred during save process of the GModel file.", e);
        }
    }

    private boolean requiresLayoutOperation() {
        long count = modelState.getRoot().getChildren().stream()
                .filter(element -> element instanceof GNode)
                .map(element -> (GNode) element)
                .filter(node -> node.getPosition().getX() == 0 && node.getPosition().getY() == 0)
                .count();
        return count > 1;
    }

    private GGraph reduceGGraph(GGraph graph) {
        for (GModelElement element : graph.getChildren()) {
            if (element instanceof GNode node) {
                node.setLayout(null);
                node.getLayoutOptions().clear();
                node.getChildren().clear();
            } else if (element instanceof GEdge edge) {
                edge.setRouterKind(null);
                edge.getCssClasses().clear();
                edge.getChildren().clear();
            }
        }
        return graph;
    }

    private static File getNotationFile(String featureModelFilePath) {
        String notationFilePath = featureModelFilePath.replaceAll("\\.uvl$", ".notation.json");
        Path path = Paths.get(notationFilePath);
        if (!Files.exists(path)) {
            LOGGER.info("GModel file does not exist: {}. Creating new notation file.", path.toUri());
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.error(e);
                throw new GLSPServerException("Could not create new GModel file: " + path.toUri(), e);
            }
        }
        return path.toFile();
    }
}
