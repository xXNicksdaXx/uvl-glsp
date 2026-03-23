/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    GEdge,
    configureModelElement,
} from '@eclipse-glsp/client';
import { ContainerModule } from 'inversify';

import { BPModelTypes } from './bp-model-types';
import { BPEdgeView } from './bp-views';

/**
 * An InversifyJS container module that registers all BP-specific diagram elements
 * on top of the base UVL diagram module.
 *
 * To add custom BP elements, register them here using {@link configureModelElement}.
 */
export const bpDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind };

    // Register BP-specific edge types
    configureModelElement(context, BPModelTypes.BP_REQUESTED,   GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.BP_BLOCKED,     GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.BP_WAITED_FOR,  GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.BP_SELECTED,    GEdge, BPEdgeView);

    configureModelElement(context, BPModelTypes.BP_CONFLICTING, GEdge, BPEdgeView);
});
