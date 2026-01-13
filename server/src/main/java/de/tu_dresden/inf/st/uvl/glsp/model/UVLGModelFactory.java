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
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.layout.LayoutEngine;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import java.util.*;

import static de.tu_dresden.inf.st.uvl.glsp.utils.FeatureUtil.getFeatureId;

public class UVLGModelFactory implements GModelFactory {

    @Inject
    protected UVLModelState modelState;

    @Inject
    protected LayoutEngine layoutEngine;

    @Override
    public void createGModel() {
        GModelRoot newRoot = createRootElement();
        modelState.updateRoot(newRoot);
        modelState.getIndex().indexFeatureModel(modelState.getFeatureModel());
        fillRootElement(newRoot);
    }

    protected GModelRoot createRootElement() {
        GGraph graph = new GGraphBuilder().build();
        graph.setId(ClientOptionsUtil.getSourceUri(modelState.getClientOptions()).orElse("root"));
        graph.setRevision(modelState.getRoot() != null ? graph.getRevision() : -1);
        return graph;
    }

    protected void fillRootElement(GModelRoot root) {
        FeatureModel model = modelState.getFeatureModel();
        GGraph graph = (GGraph) root;

        model.getFeatureMap().values().stream()
                .map(this::createFeature)
                .forEachOrdered(graph.getChildren()::add);

        model.getFeatureMap().values().stream()
                .map(Feature::getParentGroup)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::createEdge)
                .flatMap(Collection::stream)
                .forEachOrdered(graph.getChildren()::add);

        layoutEngine.layout(java.util.Optional.empty());
    }

    private GNode createFeature(final Feature feature) {
        GNodeBuilder nodeBuilder = new GNodeBuilder(UVLModelTypes.FEATURE)
                .id(getFeatureId(feature))
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(2)
                        .paddingLeft(2)
                        .paddingRight(2)
                        .paddingBottom(2.0))
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .id(getFeatureId(feature) + "_label")
                        .text(feature.getFeatureName())
                        .build());

        Optional<GNode> node = modelState.getIndex().getGModelElement(feature, GNode.class);
        if (node.isPresent()) {
            nodeBuilder.position(node.get().getPosition());
            nodeBuilder.size(node.get().getSize());
        }
        return nodeBuilder.build();
    }

    private Collection<GEdge> createEdge(final Group group) {
        List<GEdge> edges = new ArrayList<>();
        Feature source = group.getParentFeature();

        String type;
        switch (group.GROUPTYPE) {
            case ALTERNATIVE -> type = UVLModelTypes.ALTERNATIVE;
            case OPTIONAL -> type = UVLModelTypes.OPTIONAL;
            case MANDATORY -> type = UVLModelTypes.MANDATORY;
            case OR -> type = UVLModelTypes.OR;
            default -> throw new IllegalArgumentException("Unknown group type: " + group.GROUPTYPE);
        }

        for (Feature target : group.getFeatures()) {
            GEdgeBuilder edgeBuilder = new GEdgeBuilder(type)
                    .id(source.getFeatureName() + "_to_" + target.getFeatureName())
                    .addCssClass(group.GROUPTYPE.name().toLowerCase())
                    .sourceId(getFeatureId(source))
                    .targetId(getFeatureId(target))
                    .routerKind(GConstants.RouterKind.POLYLINE);

            edges.add(edgeBuilder.build());
        }

        return edges;
    }
}
