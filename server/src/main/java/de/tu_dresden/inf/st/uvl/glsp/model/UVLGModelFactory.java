package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
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
        Feature rootFeature = model.getRootFeature();
        GGraph graph = (GGraph) root;

        graph.getChildren().add(createFeature(rootFeature));
    }

    private GNode createFeature(final Feature feature) {
        GNodeBuilder nodeBuilder = new GNodeBuilder(UVLModelTypes.FEATURE)
                .id("root")
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(0)
                        .paddingLeft(0)
                        .paddingRight(0)
                        .paddingBottom(0.0)
                        .resizeContainer(true))
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .id("root" + "_label")
                        .text(feature.getFeatureName())
                        .build());
        return nodeBuilder.build();
    }
}
