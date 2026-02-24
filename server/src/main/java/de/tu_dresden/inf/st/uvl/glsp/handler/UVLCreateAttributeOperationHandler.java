/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.handler;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelState;
import de.vill.model.Attribute;
import de.vill.model.Feature;
import org.eclipse.emf.common.command.Command;
import org.eclipse.glsp.server.gmodel.GModelCreateOperationHandler;
import org.eclipse.glsp.server.operations.CreateNodeOperation;

import java.util.Optional;

public class UVLCreateAttributeOperationHandler  extends GModelCreateOperationHandler<CreateNodeOperation> {

    @Inject
    protected UVLModelState modelState;

    UVLCreateAttributeOperationHandler() {
        super(UVLModelTypes.ATTRIBUTE);
    }

    @Override
    public Optional<Command> createCommand(CreateNodeOperation operation) {
        return commandOf(() -> executeCreation(operation));
    }

    protected void executeCreation(CreateNodeOperation operation) {
        Feature parentFeature = modelState.getIndex().getUVLObject(operation.getContainerId(), Feature.class).orElseThrow(() ->
                new IllegalStateException("No parent feature found for attribute with container ID: " + operation.getContainerId()));

        // create new Attribute and add it to the parent feature
        int attributeSize = parentFeature.getAttributes().size();
        String name = "Attribute" + (attributeSize + 1);
        Attribute<Boolean> newAttribute = new Attribute<>(name, true);
        parentFeature.getAttributes().put(name, newAttribute);

        // update the model index
        modelState.updateIndex();
    }
}
