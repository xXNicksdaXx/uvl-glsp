/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.gmodel;

import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelIndex;
import de.tu_dresden.inf.st.uvl.glsp.utils.FeatureModelUtil;
import de.tu_dresden.inf.st.uvl.metamodel.model.Attribute;
import de.tu_dresden.inf.st.uvl.metamodel.model.Cardinality;
import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;

import java.util.Optional;

public class UVLFeatureFactory extends AbstractGModelFactory<Feature, GNode> {

    @Override
    protected GNode create(final Feature feature) {
        UVLModelIndex index = modelState.getIndex();
        String id = index.getIdFor(feature).orElseThrow(
                () -> new IllegalStateException("Feature not indexed: " + feature.getFeatureName())
        );

        GNodeBuilder nodeBuilder = new GNodeBuilder(UVLModelTypes.FEATURE)
                .id(id)
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(0)
                        .paddingLeft(0)
                        .paddingRight(0)
                        .paddingBottom(0.0)
                        .resizeContainer(true))
                .add(buildHeader(id, feature.getFeatureName(), feature.getCardinality()))
                .add(buildAttributeCompartment(id, feature));

        Optional<GNode> node = index.getGModelElement(feature, GNode.class);
        if (node.isPresent()) {
            nodeBuilder.position(node.get().getPosition());
            nodeBuilder.size(node.get().getSize());
        } else {
            // initialize with default position and size
            nodeBuilder.position(0, 0);
            nodeBuilder.size(64, 32);
        }
        return nodeBuilder.build();
    }

    protected GCompartment buildHeader(final String id, final String name, final Cardinality cardinality) {
        GLabel headerLabel = new GLabelBuilder(UVLModelTypes.FEATURE_NAME)
                .id(id + "_header_label")
                .text(name)
                .build();
        GCompartmentBuilder headerBuilder = new GCompartmentBuilder(DefaultTypes.COMPARTMENT_HEADER)
                .id(id + "_header")
                .layout(GConstants.Layout.HBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(4)
                        .paddingLeft(4)
                        .paddingRight(4)
                        .paddingBottom(4.0)
                        .hAlign(GConstants.HAlign.CENTER)
                        .resizeContainer(true))
                .add(headerLabel);

        if (cardinality != null) {
            GLabel cardinalityLabel = new GLabelBuilder(UVLModelTypes.CARDINALITY_LABEL)
                    .id(id + "_cardinality_label")
                    .text(FeatureModelUtil.getCardinalityText(cardinality))
                    .build();
            headerBuilder
                    .add(new GLabelBuilder(DefaultTypes.LABEL)
                            .text(" [")
                            .build())
                    .add(cardinalityLabel)
                    .add(new GLabelBuilder(DefaultTypes.LABEL)
                            .text("]")
                            .build());
        }

        return headerBuilder.build();
    }

    protected GCompartment buildAttributeCompartment(final String id, final Feature feature) {
        GCompartmentBuilder compartmentBuilder = new GCompartmentBuilder(DefaultTypes.COMPARTMENT)
                .id(id + "_attribute_compartment")
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(0)
                        .paddingLeft(0)
                        .paddingRight(0)
                        .paddingBottom(0.0)
                        .hAlign(GConstants.HAlign.LEFT)
                        .resizeContainer(true));

        int i = 0;
        for (Attribute<?> attribute : feature.getAttributes().values()) {
            compartmentBuilder.add(createAttribute(id, i, attribute));
            i++;
        }

        return compartmentBuilder.build();
    }

    protected GCompartment createAttribute(final String id, final int index, final Attribute<?> attribute) {
        String attributeId = id + "_attribute_" + index;

        return new GCompartmentBuilder(UVLModelTypes.ATTRIBUTE)
                .id(attributeId)
                .addCssClass("attribute")
                .layout(GConstants.Layout.HBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(2)
                        .paddingLeft(2)
                        .paddingRight(2)
                        .paddingBottom(2.0)
                        .resizeContainer(true))
                .add(new GLabelBuilder(UVLModelTypes.ATTRIBUTE_NAME)
                        .id(attributeId + "_name")
                        .text(attribute.getName())
                        .build())
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .text(" = ")
                        .build())
                .add(new GLabelBuilder(UVLModelTypes.ATTRIBUTE_VALUE)
                        .id(attributeId + "_value")
                        .text(formatAttributeValue(attribute))
                        .build())
                .build();
    }

    protected String formatAttributeValue(final Attribute<?> attribute) {
        return attribute.getValue().toString();
    }
}
