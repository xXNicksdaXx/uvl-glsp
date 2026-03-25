/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.bp.glsp;

import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.bp.glsp.gmodel.BPGModelFactory;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPModelState;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPModelStateImpl;
import de.tu_dresden.inf.st.uvl.glsp.UVLDiagramModule;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPSourceModelStorage;
import org.eclipse.glsp.server.features.core.model.GModelFactory;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;

public class BPDiagramModule extends UVLDiagramModule {

    @Override
    protected void configure() {
        super.configure();
        configureBPModelState(bindGModelState());
    }

    @Override
    protected Class<? extends BPModelState> bindGModelState() {
        return BPModelStateImpl.class;
    }

    @Override
    protected Class<? extends SourceModelStorage> bindSourceModelStorage() {
        return BPSourceModelStorage.class;
    }

    @Override
    protected Class<? extends GModelFactory> bindGModelFactory() {
        return BPGModelFactory.class;
    }

    protected void configureBPModelState(final Class<? extends BPModelState> bpStateClass) {
        bind(BPModelState.class).to(bpStateClass).in(Singleton.class);
    }
}

