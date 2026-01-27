/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.Group;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.*;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UVLGModelFactory implements GModelFactory {

    @Inject
    protected UVLModelState modelState;

    @Override
    public void createGModel() {
        FeatureModel featureModel = modelState.getFeatureModel();
        UVLModelIndex index = modelState.getIndex();

        GGraph newRoot = createRootElement();
        modelState.updateRoot(newRoot);

        fillRootElement(newRoot, featureModel, index);
    }

    protected GGraph createRootElement() {
        return new GGraphBuilder(DefaultTypes.GRAPH)
                .id(ClientOptionsUtil.getSourceUri(modelState.getClientOptions()).orElse("root"))
                .revision(modelState.getRoot() != null ? modelState.getRoot().getRevision() : -1)
                .build();
    }

    protected void fillRootElement(GGraph root, FeatureModel featureModel, UVLModelIndex index) {
        featureModel.getFeatureMap().values().stream()
                .map(feature -> createFeature(feature, index))
                .forEachOrdered(root.getChildren()::add);

        featureModel.getFeatureMap().values().stream()
                .map(Feature::getParentGroup)
                .filter(Objects::nonNull)
                .distinct()
                .map(group -> createEdge(group, index))
                .flatMap(Collection::stream)
                .forEachOrdered(root.getChildren()::add);
    }

    private GNode createFeature(final Feature feature, UVLModelIndex index) {
        String id = index.getIdFor(feature);

        GNodeBuilder nodeBuilder = new GNodeBuilder(UVLModelTypes.FEATURE)
                .id(id)
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(2)
                        .paddingLeft(2)
                        .paddingRight(2)
                        .paddingBottom(2.0))
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .id(id + "_label")
                        .text(feature.getFeatureName())
                        .build());

        Optional<GNode> node = index.getGModelElement(feature, GNode.class);
        if (node.isPresent()) {
            nodeBuilder.position(node.get().getPosition());
            nodeBuilder.size(node.get().getSize());
        }
        return nodeBuilder.build();
    }

    private Collection<GEdge> createEdge(final Group group, UVLModelIndex index) {
        List<GEdge> edges = new ArrayList<>();
        String groupId = index.getIdFor(group);
        String sourceId = index.getIdFor(group.getParentFeature());
        String type;
        switch (group.GROUPTYPE) {
            case ALTERNATIVE -> type = UVLModelTypes.ALTERNATIVE;
            case OPTIONAL -> type = UVLModelTypes.OPTIONAL;
            case MANDATORY -> type = UVLModelTypes.MANDATORY;
            case OR -> type = UVLModelTypes.OR;
            default -> throw new IllegalArgumentException("Unknown group type: " + group.GROUPTYPE);
        }

        for (Feature target : group.getFeatures()) {
            String targetId = index.getIdFor(target);
            GEdgeBuilder edgeBuilder = new GEdgeBuilder(type)
                    .id(groupId + "_" + targetId)
                    .addCssClass(group.GROUPTYPE.name().toLowerCase())
                    .sourceId(sourceId)
                    .targetId(targetId)
                    .routerKind(GConstants.RouterKind.POLYLINE);

            edges.add(edgeBuilder.build());
        }

        return edges;
    }
}
