/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    IViewArgs,
    RectangularNodeView,
    RenderingContext,
} from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { svg } from 'sprotty';

import { LabeledNode } from "./model";

// Workaround for typing issues: Creating elements returns a `JSX.Element` instead of a `VNode`, which causes compilation errors.
// Therefore, type the VNode elements as `any` to avoid these problems.
// See also: https://github.com/eclipse/sprotty/issues/178
// The solutions described there did not work in this case.
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class LabeledNodeView extends RectangularNodeView {

    override render(node: LabeledNode, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!this.isVisible(node, context)) {
            return undefined;
        }

        return (
            <g>
                <rect
                    x={0}
                    y={0}
                    width={Math.max(0, node.bounds.width)}
                    height={Math.max(0, node.bounds.height)}
                    class-sprotty-node={true}
                    class-selected={node.selected}
                    class-mouseover={node.hoverFeedback}
                />

                {this.renderSeparatorLine(node)}

                {context.renderChildren(node)}
            </g>
        );
    }

    protected renderSeparatorLine(node: LabeledNode): VNode {
        return (
            <line
                x1={0}
                y1={Math.max(0, node.headerContainer.bounds.height)}
                x2={Math.max(0, node.bounds.width)}
                y2={Math.max(0, node.headerContainer.bounds.height)}
                class-separator-line={true}
            />
        );
    }
}
