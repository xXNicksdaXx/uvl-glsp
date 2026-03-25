/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    GEdge,
    CircularNode,
    CircularNodeView,
    configureModelElement,
    GNode,
    RectangularNode,
    RectangularNodeView
} from '@eclipse-glsp/client';
import { ContainerModule } from 'inversify';

import { LabeledNode, LabeledNodeView } from "uvl-sprotty";

import { BPModelTypes } from './bp-model-types';
import { BPEdgeView, TriangularNodeView } from './views';

/**
 * An InversifyJS container module that registers all BP-specific diagram elements
 * on top of the base UVL diagram module.
 */
export const bpDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = { bind, unbind, isBound, rebind };

    configureModelElement(context, BPModelTypes.B_THREAD, LabeledNode, LabeledNodeView);

    configureModelElement(context, BPModelTypes.REQUESTED_EVENT, CircularNode, CircularNodeView);
    configureModelElement(context, BPModelTypes.WAITED_FOR_EVENT, RectangularNode, RectangularNodeView);
    configureModelElement(context, BPModelTypes.BLOCKED_EVENT, GNode, TriangularNodeView);

    configureModelElement(context, BPModelTypes.REQUESTED, GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.BLOCKED, GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.WAITED_FOR, GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.SELECTED, GEdge, BPEdgeView);
    configureModelElement(context, BPModelTypes.CONFLICTING, GEdge, BPEdgeView);
});
