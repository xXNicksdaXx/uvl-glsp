/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.gmodel.generic;

import de.tu_dresden.inf.st.uvl.glsp.model.UVLModelState;
import de.tu_dresden.inf.st.uvl.metamodel.model.UVLObject;
import org.eclipse.glsp.graph.GModelElement;

import com.google.inject.Inject;

import java.util.Collection;

public abstract class AbstractCollectedGModelFactory<E extends UVLObject, T extends GModelElement> extends AbstractGModelFactory {

    @Inject
    protected UVLModelState modelState;

    protected abstract Collection<T> create(E object);
}
