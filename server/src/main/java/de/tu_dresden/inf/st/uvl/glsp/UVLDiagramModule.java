/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp;

import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.glsp.layout.UVLTreeLayoutEngine;
import org.eclipse.glsp.server.actions.ActionHandler;
import org.eclipse.glsp.server.di.DiagramModule;
import org.eclipse.glsp.server.di.MultiBinding;
import org.eclipse.glsp.server.diagram.DiagramConfiguration;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;
import org.eclipse.glsp.server.layout.LayoutEngine;
import org.eclipse.glsp.server.operations.OperationHandler;

import de.tu_dresden.inf.st.uvl.glsp.model.UVLGModelFactory;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelState;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelStateImpl;
import de.tu_dresden.inf.st.uvl.glsp.model.UVLSourceModelStorage;
import de.tu_dresden.inf.st.uvl.glsp.palette.UVLToolPaletteItemProvider;

public class UVLDiagramModule extends DiagramModule {

    @Override
    protected Class<? extends DiagramConfiguration> bindDiagramConfiguration() {
        return UVLDiagramConfiguration.class;
    }

    @Override
    protected Class<? extends UVLModelState> bindGModelState() {
        return UVLModelStateImpl.class;
    }

    @Override
    protected Class<? extends SourceModelStorage> bindSourceModelStorage() {
        return UVLSourceModelStorage.class;
    }

    @Override
    protected Class<? extends GModelFactory> bindGModelFactory() {
        return UVLGModelFactory.class;
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
    protected Class<? extends LayoutEngine> bindLayoutEngine() {
        return UVLTreeLayoutEngine.class;
    }

    @Override
    public String getDiagramType() {
        return "uvl-diagram";
    }

    @Override
    protected void configure() {
        super.configure();
        configureUVLModelState(bindGModelState());
    }

    protected void configureUVLModelState(final Class<? extends UVLModelState> uvlStateClass) {
        bind(UVLModelState.class).to(uvlStateClass).in(Singleton.class);
    }
}
