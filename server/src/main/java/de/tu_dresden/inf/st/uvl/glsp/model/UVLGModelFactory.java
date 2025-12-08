package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.notation.ElementNotation;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import static de.tu_dresden.inf.st.uvl.glsp.utils.FeatureUtil.getFeatureId;
import static de.tu_dresden.inf.st.uvl.glsp.utils.NotationUtil.applyNotationData;

public class UVLGModelFactory implements GModelFactory {

    @Inject
    protected UVLModelState modelState;

    @Override
    public void createGModel() {
        GModelRoot newRoot = createRootElement();
        modelState.updateRoot(newRoot);
        fillRootElement(newRoot);
    }

    protected GModelRoot createRootElement() {
        GGraph graph = new GGraphBuilder().build();
        graph.setId(ClientOptionsUtil.getSourceUri(modelState.getClientOptions()).orElse("root"));
        graph.setRevision(modelState.getRoot() != null ? graph.getRevision() : -1);
        return graph;
    }

    protected void fillRootElement(GModelRoot root) {
        FeatureModel model = modelState.getUVLModel();
        GGraph graph = (GGraph) root;

        model.getFeatureMap().values().stream()
                .map(this::createFeature)
                .forEachOrdered(graph.getChildren()::add);
    }

    private GNode createFeature(final Feature feature) {
        GNodeBuilder nodeBuilder = new GNodeBuilder(UVLModelTypes.FEATURE)
                .id(getFeatureId(feature))
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(0)
                        .paddingLeft(0)
                        .paddingRight(0)
                        .paddingBottom(0.0))
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .id(getFeatureId(feature) + "_label")
                        .text(feature.getFeatureName())
                        .build());

        ElementNotation notation = modelState.getNotationData().getElementNotation(feature.getFeatureName());
        applyNotationData(notation, nodeBuilder);
        return nodeBuilder.build();
    }
}
