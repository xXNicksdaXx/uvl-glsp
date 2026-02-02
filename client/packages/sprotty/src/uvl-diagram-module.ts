/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    configureDefaultModelElements,
    configureModelElement,
    ConsoleLogger,
    ContainerConfiguration, defaultModule,
    DefaultTypes,
    GEdge,
    GEdgeView,
    GLabelView,
    GModelElement,
    GNode,
    GShapedPreRenderedElement,
    helperLineModule,
    HelperLineType,
    initializeDiagramContainer,
    layoutableChildFeature,
    layoutModule,
    LogLevel,
    overrideModelElement,
    PreRenderedView,
    RectangularNodeView,
    TYPES
} from '@eclipse-glsp/client';
import { Container, ContainerModule } from 'inversify';

import { UVLModelTypes } from 'uvl-common';

import 'balloon-css/balloon.min.css';
import '../css/diagram.css';

import { FeatureCompartmentSelectionFeedback } from './features/feedback';
import { EditableGLabel } from "./model";
import { CircleEdgeView } from "./views";

const uvlDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
    const context = {bind, unbind, isBound, rebind};
    rebind(TYPES.ILogger).to(ConsoleLogger).inSingletonScope();
    rebind(TYPES.LogLevel).toConstantValue(LogLevel.warn);

    bind(TYPES.IVNodePostprocessor).to(FeatureCompartmentSelectionFeedback);
    bind(TYPES.IHelperLineOptions).toConstantValue({
        elementLines: [
            HelperLineType.Left, HelperLineType.Center, HelperLineType.Right,
            HelperLineType.Top, HelperLineType.Middle, HelperLineType.Bottom
        ],
        viewportLines: [], // do not show alignment lines for viewport
        alignmentElementFilter: (element: GModelElement) => true,
        minimumMoveDelta: { x: 10, y: 10 },
        alignmentEpsilon: 0.5
    });

    configureDefaultModelElements(context);
    overrideModelElement(context, DefaultTypes.SHAPE_PRE_RENDERED, GShapedPreRenderedElement, PreRenderedView, {
        enable: [layoutableChildFeature]
    });

    overrideModelElement(context, DefaultTypes.LABEL, EditableGLabel, GLabelView)

    // Register custom model elements and their views
    configureModelElement(context, UVLModelTypes.FEATURE, GNode, RectangularNodeView);

    configureModelElement(context, UVLModelTypes.MANDATORY, GEdge, CircleEdgeView);
    configureModelElement(context, UVLModelTypes.OPTIONAL, GEdge, CircleEdgeView);
    configureModelElement(context, UVLModelTypes.ALTERNATIVE, GEdge, GEdgeView);
    configureModelElement(context, UVLModelTypes.GROUP_CARDINALITY, GEdge, GEdgeView);
    configureModelElement(context, UVLModelTypes.OR, GEdge, GEdgeView);
});

/**
 * Initializes the diagram DI container with the UVL diagram module and all required Sprotty/GLSP modules.
 *
 * @param container The InversifyJS container to configure.
 * @param containerConfiguration Additional container configuration modules.
 * @returns The initialized container.
 */
export function initializeUvlDiagramContainer(container: Container, ...containerConfiguration: ContainerConfiguration): Container {
    return initializeDiagramContainer(container, defaultModule, helperLineModule, layoutModule, uvlDiagramModule, ...containerConfiguration);
}
