/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.bp.glsp.gmodel;

import de.tu_dresden.inf.st.uvl.bp.glsp.BPModelTypes;
import de.tu_dresden.inf.st.uvl.bp.glsp.utils.BTypeUtil;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.gmodel.UVLFeatureFactory;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelIndex;
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

import static de.tu_dresden.inf.st.uvl.bp.glsp.utils.BTypeUtil.*;

public class BPFeatureFactory extends UVLFeatureFactory {

    @Override
    protected GNode create(final Feature feature) {
        if (isBThread(feature)) {
            return createBThread(feature);
        }


        return createFeature(feature);
    }

    protected GNode createBThread(final Feature feature) {
        UVLModelIndex index = modelState.getIndex();
        String id = index.getIdFor(feature).orElseThrow(
                () -> new IllegalStateException("Feature not indexed: " + feature.getFeatureName())
        );

        GNodeBuilder nodeBuilder = new GNodeBuilder(BPModelTypes.B_THREAD)
                .id(id)
                .layout(GConstants.Layout.VBOX)
                .layoutOptions(new GLayoutOptions()
                        .paddingTop(0)
                        .paddingLeft(0)
                        .paddingRight(0)
                        .paddingBottom(0.0)
                        .resizeContainer(true))
                .add(buildBThreadHeader(id, feature.getFeatureName(), feature.getCardinality()))
                .add(buildAttributeCompartment(id, feature));

        applyNodeData(nodeBuilder, id);
        return nodeBuilder.build();
    }

    protected GCompartment buildBThreadHeader(final String id, final String name, final Cardinality cardinality) {
        GLabel threadLabel = new GLabelBuilder(DefaultTypes.LABEL)
                .text("<<BThread>> ")
                .build();
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
                .add(threadLabel)
                .add(headerLabel);

        if (cardinality != null) {
            addCardinality(cardinality, id,  headerBuilder);
        }

        return headerBuilder.build();
    }

    @Override
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
            if (isBThreadAttribute(attribute)) {
                continue;
//            } else if (isBEventAttribute(attribute)) {
//                compartmentBuilder.add(createEvent(id, i, attribute));
            } else {
                compartmentBuilder.add(createAttribute(id, i, attribute));
            }
            i++;
        }

        return compartmentBuilder.build();
    }

    public GNode createEvent(String id, int index, Attribute<?> attribute) {
        BTypeUtil.BEventType eventType = BTypeUtil.getBEventType(attribute);
        return switch (eventType) {
            case REQUESTED -> createRequestedEvent(id, index, attribute);
            case BLOCKED -> createBlockedEvent(id, index, attribute);
            case WAITED_FOR -> createWaitedForEvent(id, index, attribute);
            default -> throw new IllegalArgumentException("Unknown BEvent type for attribute: " + attribute.getName());
        };
    }

    protected GNode createRequestedEvent(String id, int index, Attribute<?> attribute) {
        String attributeId = id + "_attribute_" + index;

        GNodeBuilder nodeBuilder = new GNodeBuilder(BPModelTypes.REQUESTED_EVENT)
                .id(attributeId)
                .addCssClass("bp-event")
                .addCssClass("bp-event-requested")
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .text(attribute.getName())
                        .build()
                );

        applyNodeData(nodeBuilder, id);
        return nodeBuilder.build();
    }

    protected GNode createBlockedEvent(String id, int index, Attribute<?> attribute) {
        String attributeId = id + "_attribute_" + index;

        GNodeBuilder nodeBuilder = new GNodeBuilder(BPModelTypes.BLOCKED_EVENT)
                .id(attributeId)
                .addCssClass("bp-event")
                .addCssClass("bp-event-blocked")
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .text(attribute.getName())
                        .build()
                );

        applyNodeData(nodeBuilder, id);
        return nodeBuilder.build();
    }

    protected GNode createWaitedForEvent(String id, int index, Attribute<?> attribute) {
        String attributeId = id + "_attribute_" + index;

        GNodeBuilder nodeBuilder = new GNodeBuilder(BPModelTypes.WAITED_FOR_EVENT)
                .id(attributeId)
                .addCssClass("bp-event")
                .addCssClass("bp-event-waited-for")
                .add(new GLabelBuilder(DefaultTypes.LABEL)
                        .text(attribute.getName())
                        .build()
                );

        applyNodeData(nodeBuilder, id);
        return nodeBuilder.build();
    }
}
