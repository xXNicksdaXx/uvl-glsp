/****************************************************************************
 *
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { GEdge, GEdgeView, Point, RenderingContext } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { svg } from 'sprotty';

// Workaround for typing issues: Creating elements returns a `JSX.Element` instead of a `VNode`, which causes compilation errors.
// Therefore, type the VNode elements as `any` to avoid these problems.
// See also: https://github.com/eclipse/sprotty/issues/178
// The solutions described there did not work in this case.
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class CircleEdgeView extends GEdgeView {

    protected override renderAdditionals(edge: GEdge, segments: Point[], context: RenderingContext): VNode[] {
        const additionals = super.renderAdditionals(edge, segments, context);
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];

        const offset = 2; // offset from the end of the edge
        const radius = 4; // circle radius

        const dx = p2.x - p1.x;
        const dy = p2.y - p1.y;
        const len = Math.sqrt(dx * dx + dy * dy) || 1;
        const nx = dx / len;
        const ny = dy / len;

        const cx = p2.x - nx * offset;
        const cy = p2.y - ny * offset;

        const circle = (
            <circle
                class-sprotty-edge={true}
                class-circle={true}
                cx={cx}
                cy={cy}
                r={radius}
            />
        );

        additionals.push(circle);
        return additionals;
    }
}