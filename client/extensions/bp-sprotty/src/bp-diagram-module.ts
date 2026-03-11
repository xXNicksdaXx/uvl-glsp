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
    ContainerConfiguration
} from '@eclipse-glsp/client';
import { Container, ContainerModule } from 'inversify';
import { initializeUvlDiagramContainer } from 'uvl-sprotty';

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

/**
 * Initializes the diagram DI container with the UVL base modules AND the BP extension module.
 *
 * This is the entry point used by the BP webview. Pass additional
 * {@link ContainerConfiguration} modules to extend behaviour further.
 *
 * @param container  The InversifyJS container to configure.
 * @param containerConfiguration  Additional container configuration modules.
 * @returns The initialized container.
 */
export function initializeBpDiagramContainer(container: Container, ...containerConfiguration: ContainerConfiguration): Container {
    return initializeUvlDiagramContainer(container, bpDiagramModule, ...containerConfiguration);
}


