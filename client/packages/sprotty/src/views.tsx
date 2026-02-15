/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
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
    protected OFFSET: number = 2;
    protected RADIUS: number = 4;

    protected override renderAdditionals(edge: GEdge, segments: Point[], context: RenderingContext): VNode[] {
        const additionals = super.renderAdditionals(edge, segments, context);
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];

        const dx = p2.x - p1.x;
        const dy = p2.y - p1.y;
        const len = Math.sqrt(dx * dx + dy * dy) || 1;
        const nx = dx / len;
        const ny = dy / len;

        const cx = p2.x - nx * this.OFFSET;
        const cy = p2.y - ny * this.OFFSET;

        const circle = (
            <circle
                class-sprotty-edge={true}
                class-circle={true}
                cx={cx}
                cy={cy}
                r={this.RADIUS}
            />
        );

        additionals.push(circle);
        return additionals;
    }
}

@injectable()
export class SectorEdgeView extends GEdgeView {
    protected OFFSET: number = 2;
    protected RADIUS: number = 16;

    protected override renderAdditionals(edge: GEdge, segments: Point[], context: RenderingContext): VNode[] {
        const additionals = super.renderAdditionals(edge, segments, context);
        const neighbors: GEdge[] = this.getNeighborhood(edge);

        for (const neighbor of neighbors) {
            const source = segments[0];
            const originalTarget = segments[1];

            const neighborSegments = this.edgeRouterRegistry.route(neighbor)
            const neighborTarget = neighborSegments[1];

            let s1 = this.getSectorPoint(source, originalTarget, this.RADIUS);
            let s2 = this.getSectorPoint(source, neighborTarget, this.RADIUS);

            if (this.isToTheRight(source, s1, s2)) {
                continue;
            }

            const largeArcFlag = this.calculateLargeArcFlag(s1, s2, source);

            const arc = (
                <path
                    class-sprotty-edge={true}
                    class-sector={true}
                    d={`M ${s1.x} ${s1.y} A ${this.RADIUS} ${this.RADIUS} 0 ${largeArcFlag} 1 ${s2.x} ${s2.y}`}
                />
            );
            additionals.push(arc);
        }

        return additionals;
    }

    protected getNeighborhood(edge: GEdge): GEdge[] {
        const parent = edge.parent;
        if (!parent) {
            return [];
        }

        return parent.children
            .filter(element => element instanceof GEdge && element.type === edge.type)
            .map(element => element as GEdge)
            .filter(e => e.source === edge.source);
    }

    protected getSectorPoint(source: Point, target: Point, radius: number): Point {
        const dx = target.x - source.x;
        const dy = target.y - source.y;
        const len = Math.sqrt(dx * dx + dy * dy) || 1;
        return {
            x: source.x + (dx / len) * radius,
            y: source.y + (dy / len) * radius
        };
    }

    protected isToTheRight(source: Point, target: Point, point: Point): boolean {
        const cross = (target.x - source.x) * (point.y - source.y) - (target.y - source.y) * (point.x - source.x);
        return cross < 0;
    }

    protected calculateLargeArcFlag(s1: Point, s2: Point, source: Point): number {
        const dx1 = s1.x - source.x;
        const dy1 = s1.y - source.y;
        const dx2 = s2.x - source.x;
        const dy2 = s2.y - source.y;
        const dot = (dx1 * dx2 + dy1 * dy2) / (this.RADIUS * this.RADIUS);
        const clamped = Math.max(-1, Math.min(1, dot));
        const angle = Math.acos(clamped);
        return angle > Math.PI ? 1 : 0;
    }
}