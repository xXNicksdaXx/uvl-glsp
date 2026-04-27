/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.gmodel;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.bp.glsp.BPModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.gmodel.UVLAttributeFactory;
import de.tu_dresden.inf.st.uvl.glsp.gmodel.generic.AbstractCollectedGModelFactory;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelIndex;
import de.tu_dresden.inf.st.uvl.metamodel.model.BPFeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;

public class BPAdditionalElementsFactory
    extends AbstractCollectedGModelFactory<FeatureModel, GNode> {

  @Inject protected UVLAttributeFactory attributeFactory;

  @Override
  public Collection<GNode> create(FeatureModel featureModel) {
    if (featureModel instanceof BPFeatureModel) {
      return createAdditionalElements((BPFeatureModel) featureModel);
    }
    return List.of();
  }

  public Collection<GNode> createAdditionalElements(final BPFeatureModel featureModel) {
    List<GNode> additionalElements = new ArrayList<>();

    if (featureModel.getConfig() != null) {
      additionalElements.add(createConfig(featureModel.getConfig()));
    }

    if (featureModel.getEnv() != null) {
      additionalElements.add(createEnv(featureModel.getEnv()));
    }

    return additionalElements;
  }

  public GNode createConfig(final Feature config) {
    return createContextFeature(config, BPModelTypes.BP_CONFIG);
  }

  public GNode createEnv(final Feature env) {
    return createContextFeature(env, BPModelTypes.BP_ENV);
  }

  public GNode createContextFeature(final Feature feature, String type) {
    UVLModelIndex index = modelState.getIndex();
    String id =
        index
            .getIdFor(feature)
            .orElseThrow(
                () ->
                    new IllegalStateException("Feature not indexed: " + feature.getFeatureName()));

    GNodeBuilder nodeBuilder =
        new GNodeBuilder(type)
            .id(id)
            .layout(GConstants.Layout.VBOX)
            .layoutOptions(
                new GLayoutOptions()
                    .paddingTop(0)
                    .paddingLeft(0)
                    .paddingRight(0)
                    .paddingBottom(0.0)
                    .resizeContainer(true))
            .add(buildContextHeader(id, feature.getFeatureName()))
            .add(buildAttributeCompartment(id, feature));

    applyNodeData(nodeBuilder, id);
    return nodeBuilder.build();
  }

  protected GCompartment buildContextHeader(final String id, final String name) {
    return new GCompartmentBuilder(DefaultTypes.COMPARTMENT_HEADER)
        .id(id + "_header")
        .layout(GConstants.Layout.VBOX)
        .layoutOptions(
            new GLayoutOptions()
                .paddingTop(4)
                .paddingLeft(4)
                .paddingRight(4)
                .paddingBottom(4.0)
                .hAlign(GConstants.HAlign.CENTER)
                .resizeContainer(true))
        .add(
            new GLabelBuilder(DefaultTypes.LABEL)
                .text("<<Context>>")
                .addCssClass("cursive-title")
                .build())
        .add(
            new GLabelBuilder(UVLModelTypes.FEATURE_NAME)
                .id(id + "_header_label")
                .addCssClass("bold-title")
                .text(name)
                .build())
        .build();
  }

  protected GCompartment buildAttributeCompartment(final String id, final Feature feature) {
    GCompartmentBuilder compartmentBuilder =
        new GCompartmentBuilder(DefaultTypes.COMPARTMENT)
            .id(id + "_attribute_compartment")
            .layout(GConstants.Layout.VBOX)
            .layoutOptions(
                new GLayoutOptions()
                    .paddingTop(0)
                    .paddingLeft(0)
                    .paddingRight(0)
                    .paddingBottom(0.0)
                    .hAlign(GConstants.HAlign.LEFT)
                    .resizeContainer(true));

    feature.getAttributes().values().stream()
        .filter(attribute -> !attribute.getName().equals("type"))
        .map(attributeFactory::create)
        .forEach(compartmentBuilder::add);

    return compartmentBuilder.build();
  }
}
