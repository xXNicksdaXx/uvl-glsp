/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.handler;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelState;
import de.vill.model.Feature;
import de.vill.model.Group;
import org.eclipse.emf.common.command.Command;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.server.gmodel.GModelCreateOperationHandler;
import org.eclipse.glsp.server.operations.CreateEdgeOperation;

import java.util.Optional;

import static de.tu_dresden.inf.st.uvl.glsp.utils.GroupUtil.convertModelTypeToGroupType;

public class UVLCreateMandatoryEdgeOperationHandler extends GModelCreateOperationHandler<CreateEdgeOperation> {

    @Inject
    protected UVLModelState modelState;

    UVLCreateMandatoryEdgeOperationHandler() {
        super(UVLModelTypes.MANDATORY);
    }

    @Override
    public Optional<Command> createCommand(CreateEdgeOperation operation) {
        return edgeAlreadyExists(operation)
                ? doNothing()
                : commandOf(() -> executeCreation(operation));
    }

    protected void executeCreation(CreateEdgeOperation operation) {
        Feature sourceFeature = modelState.getIndex()
                .getUVLObject(operation.getSourceElementId(), Feature.class)
                .orElseThrow(
                        () -> new IllegalStateException("Source feature not found for ID: " + operation.getSourceElementId()));
        Feature targetFeature = modelState.getIndex()
                .getUVLObject(operation.getTargetElementId(), Feature.class)
                .orElseThrow(
                        () -> new IllegalStateException("Target feature not found for ID: " + operation.getTargetElementId()));

        // check for an existing edge between the two features (must be replaced)
        Optional<GEdge> existingEdge = modelState.getIndex().getAllByClass(GEdge.class).stream().filter(
                gEdge -> gEdge.getSourceId().equals(operation.getSourceElementId())
                        && gEdge.getTargetId().equals(operation.getTargetElementId())).findFirst();

        if (existingEdge.isPresent()) {
            // check which type of edge already exists
            Group.GroupType existingType = convertModelTypeToGroupType(existingEdge.get().getType());

            Group existingGroup = sourceFeature.getChildren().stream()
                    .filter(group -> group.GROUPTYPE == existingType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Existing group not found for type: " + existingType));

            existingGroup.getFeatures().remove(targetFeature);
            if (existingGroup.getFeatures().isEmpty()) {
                sourceFeature.getChildren().remove(existingGroup);
            }

            modelState.getRoot().getChildren().remove(existingEdge.get());
        }

        Optional<Group> existingGroup = sourceFeature.getChildren().stream()
                .filter(group -> group.GROUPTYPE == Group.GroupType.MANDATORY)
                .findFirst();

        if (existingGroup.isPresent()) {
            existingGroup.get().getFeatures().add(targetFeature);
        } else {
            Group newGroup = new Group(Group.GroupType.MANDATORY);
            newGroup.setParentFeature(sourceFeature);
            newGroup.getFeatures().add(targetFeature);
            sourceFeature.getChildren().add(newGroup);
        }

        modelState.updateIndex();
    }

    private boolean edgeAlreadyExists(CreateEdgeOperation operation) {
        Optional<GEdge> existingEdge = modelState.getIndex().getAllByClass(GEdge.class).stream().filter(
                gEdge -> gEdge.getSourceId().equals(operation.getSourceElementId())
                        && gEdge.getTargetId().equals(operation.getTargetElementId())).findFirst();

        return existingEdge.map(gEdge -> gEdge.getType().equals(UVLModelTypes.MANDATORY)).orElse(false);
    }
}
