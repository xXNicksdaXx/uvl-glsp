/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    bindAsService,
    configureDefaultModelElements,
    configureModelElement,
    ConsoleLogger,
    ContainerConfiguration,
    defaultModule,
    DefaultTypes,
    GEdge,
    GEdgeView,
    GLabel,
    GLabelView,
    GLSPPolylineEdgeRouter,
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

import { CenteredAnchor } from "./features/center-anchor-computer";
import { FeatureCompartmentSelectionFeedback } from './features/feedback';
import { EditableGLabel } from "./model";
import { CircleEdgeView, DoubleArrowEdgeView, SectorEdgeView, SingleArrowEdgeView } from "./views";
import { UVLPolylineEdgeRouter } from "./features/uvl-polyline-edge-router";

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
        viewportLines: [],
        minimumMoveDelta: { x: 16, y: 16 },
        alignmentEpsilon: 0.5
    });

    bindAsService(context, TYPES.IAnchorComputer, CenteredAnchor);
    bind(UVLPolylineEdgeRouter).toSelf().inSingletonScope();
    rebind(GLSPPolylineEdgeRouter).toService(UVLPolylineEdgeRouter);

    configureDefaultModelElements(context);
    overrideModelElement(context, DefaultTypes.SHAPE_PRE_RENDERED, GShapedPreRenderedElement, PreRenderedView, {
        enable: [layoutableChildFeature]
    });

    overrideModelElement(context, DefaultTypes.LABEL, EditableGLabel, GLabelView)

    // Register custom model elements and their views
    configureModelElement(context, UVLModelTypes.FEATURE, GNode, RectangularNodeView);

    configureModelElement(context, UVLModelTypes.MANDATORY, GEdge, CircleEdgeView);
    configureModelElement(context, UVLModelTypes.OPTIONAL, GEdge, CircleEdgeView);
    configureModelElement(context, UVLModelTypes.ALTERNATIVE, GEdge, SectorEdgeView);
    configureModelElement(context, UVLModelTypes.GROUP_CARDINALITY, GEdge, GEdgeView);
    configureModelElement(context, UVLModelTypes.OR, GEdge, SectorEdgeView);

    configureModelElement(context, UVLModelTypes.IMPLICATION, GEdge, SingleArrowEdgeView);
    configureModelElement(context, UVLModelTypes.EQUIVALENCE, GEdge, DoubleArrowEdgeView);

    configureModelElement(context, UVLModelTypes.CARDINALITY_LABEL, EditableGLabel, GLabelView);
    configureModelElement(context, UVLModelTypes.CONSTRAINT_LABEL, GLabel, GLabelView)
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
