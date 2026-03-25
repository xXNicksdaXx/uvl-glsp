/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    GEdge,
    GEdgeView,
    GNode,
    GPort,
    GShapeElement,
    Hoverable,
    IViewArgs,
    Point,
    RenderingContext,
    Selectable,
    ShapeView
} from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { svg } from 'sprotty';

// Workaround for typing issues with JSX / VNode – see uvl-sprotty views.tsx
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

/**
 * Simple dashed edge view used to render all BP event constraint edges.
 *
 * The dashed stroke visually distinguishes BP event constraints from
 * regular UVL implication / equivalence edges. Add further styling or
 * arrowhead rendering here as the BP feature set grows.
 */
@injectable()
export class BPEdgeView extends GEdgeView {
    protected override renderLine(edge: GEdge, segments: Point[], _context: RenderingContext, _args?: IViewArgs): VNode {
        const firstPoint = segments[0];
        let path = `M ${firstPoint.x},${firstPoint.y}`;
        for (let i = 1; i < segments.length; i++) {
            const p = segments[i];
            path += ` L ${p.x},${p.y}`;
        }
        return (
            <path
                class-sprotty-edge={true}
                class-bp-edge={true}
                stroke-dasharray="6,4"
                d={path}
            />
        ) as unknown as VNode;
    }
}

@injectable()
export class TriangularNodeView extends ShapeView {
    render(node: Readonly<GShapeElement & Hoverable & Selectable>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(node, context)) {
            return undefined;
        }

        const width = Math.max(0, node.bounds.width);
        const height = Math.max(0, node.bounds.height);
        const triangle = `M ${width / 2},0 L ${width},${height} L 0,${height} Z`;

        return <g>
            <path
                class-sprotty-node={node instanceof GNode}
                class-sprotty-port={node instanceof GPort}
                class-mouseover={node.hoverFeedback}
                class-selected={node.selected}
                x="0" y="0"
                d={triangle}>
            </path>
            {context.renderChildren(node)}
        </g>;
    }
}
