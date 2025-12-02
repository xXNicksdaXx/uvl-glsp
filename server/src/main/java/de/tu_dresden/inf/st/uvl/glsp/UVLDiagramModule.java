/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp;

import org.eclipse.glsp.server.actions.ActionHandler;
import org.eclipse.glsp.server.di.DiagramModule;
import org.eclipse.glsp.server.di.MultiBinding;
import org.eclipse.glsp.server.diagram.DiagramConfiguration;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;
import org.eclipse.glsp.server.gmodel.GModelStorage;
import org.eclipse.glsp.server.operations.OperationHandler;

import de.tu_dresden.inf.st.uvl.glsp.palette.UVLToolPaletteItemProvider;

public class UVLDiagramModule extends DiagramModule {

    @Override
    protected Class<? extends DiagramConfiguration> bindDiagramConfiguration() {
        return UVLDiagramConfiguration.class;
    }

    @Override
    protected Class<? extends SourceModelStorage> bindSourceModelStorage() {
        return GModelStorage.class;
    }

    @Override
    protected Class<? extends GModelFactory> bindGModelFactory() {
        return GModelFactory.NullImpl.class;
    }

    @Override
    protected void configureActionHandlers(final MultiBinding<ActionHandler> binding) {
        super.configureActionHandlers(binding);

    }

    @Override
    protected void configureOperationHandlers(final MultiBinding<OperationHandler<?>> binding) {
        super.configureOperationHandlers(binding);

    }

    @Override
    protected Class<? extends ToolPaletteItemProvider> bindToolPaletteItemProvider() {
        return UVLToolPaletteItemProvider.class;
    }

    @Override
    public String getDiagramType() {
        return "uvl-diagram";
    }
}
