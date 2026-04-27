/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.glsp.gmodel;

import static de.tu_dresden.inf.st.uvl.glsp.utils.FeatureModelUtil.getComplexConstraints;

import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.gmodel.generic.AbstractSingleGModelFactory;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelIndex;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;
import java.util.Collection;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;

public class UVLConstraintBoxFactory extends AbstractSingleGModelFactory<FeatureModel, GNode> {

  @Override
  public GNode create(FeatureModel featureModel) {
    return createConstraintBox(getComplexConstraints(featureModel));
  }

  protected GNode createConstraintBox(final Collection<Constraint> constraints) {
    GNodeBuilder boxBuilder =
        new GNodeBuilder(UVLModelTypes.CONSTRAINT_BOX)
            .id("constraint_box")
            .layout(GConstants.Layout.VBOX)
            .layoutOptions(
                new GLayoutOptions()
                    .paddingTop(0)
                    .paddingLeft(0)
                    .paddingRight(0)
                    .paddingBottom(0.0)
                    .resizeContainer(true))
            .add(
                new GCompartmentBuilder(DefaultTypes.COMPARTMENT_HEADER)
                    .id("constraint_box_header")
                    .layout(GConstants.Layout.HBOX)
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
                            .id("constraint_box_header_label")
                            .addCssClass("bold-title")
                            .text("Constraints")
                            .build())
                    .build())
            .add(buildConstraintCompartment(constraints));

    applyNodeData(boxBuilder, "constraint_box");
    return boxBuilder.build();
  }

  protected GCompartment buildConstraintCompartment(final Collection<Constraint> constraints) {
    GCompartmentBuilder compartmentBuilder =
        new GCompartmentBuilder(DefaultTypes.COMPARTMENT)
            .id("constraint_box_compartment")
            .layout(GConstants.Layout.VBOX)
            .layoutOptions(
                new GLayoutOptions()
                    .paddingTop(0)
                    .paddingLeft(0)
                    .paddingRight(0)
                    .paddingBottom(0.0)
                    .hAlign(GConstants.HAlign.LEFT)
                    .resizeContainer(true));

    constraints.stream().map(this::createConstraint).forEachOrdered(compartmentBuilder::add);

    return compartmentBuilder.build();
  }

  protected GCompartment createConstraint(final Constraint constraint) {
    UVLModelIndex index = modelState.getIndex();
    String constraintId =
        index
            .getIdFor(constraint)
            .orElseThrow(
                () ->
                    new IllegalStateException("Constraint not indexed: " + constraint.toString()));

    return new GCompartmentBuilder(UVLModelTypes.CONSTRAINT)
        .id(constraintId)
        .layout(GConstants.Layout.HBOX)
        .layoutOptions(
            new GLayoutOptions()
                .paddingTop(2)
                .paddingLeft(2)
                .paddingRight(2)
                .paddingBottom(2.0)
                .resizeContainer(true))
        .add(
            new GLabelBuilder(UVLModelTypes.CONSTRAINT_TEXT)
                .id(constraintId + "_text")
                .text(constraint.toString(false, ""))
                .build())
        .build();
  }
}
