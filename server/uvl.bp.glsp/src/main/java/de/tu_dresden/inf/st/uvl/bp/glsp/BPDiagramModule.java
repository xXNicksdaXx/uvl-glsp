/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.bp.glsp;

import de.tu_dresden.inf.st.uvl.glsp.UVLDiagramModule;
import de.tu_dresden.inf.st.uvl.bp.glsp.model.BPSourceModelStorage;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;

public class BPDiagramModule extends UVLDiagramModule {

    @Override
    protected Class<? extends SourceModelStorage> bindSourceModelStorage() {
        return BPSourceModelStorage.class;
    }
}

