/****************************************************************************
 *
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { GModelElement, IVNodePostprocessor } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { svg } from 'sprotty';

import { FeatureCompartment } from '../model';

// Workaround for typing issues: Creating elements returns a `JSX.Element` instead of a `VNode`, which causes compilation errors.
// Therefore, type the VNode elements as `any` to avoid these problems.
// See also: https://github.com/eclipse/sprotty/issues/178
// The solutions described there did not work in this case.
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class FeatureCompartmentSelectionFeedback implements IVNodePostprocessor {
    decorate(vnode: VNode, element: GModelElement): VNode {
        if (element instanceof FeatureCompartment && (element.hoverFeedback || element.selected)) {
            const vPadding = 1;
            const hPadding = 2;

            const feedback: any = (
                <rect
                    x={-hPadding}
                    y={-vPadding}
                    width={element.bounds.width + 2 * hPadding}
                    height={element.bounds.height + 2 * vPadding}
                    class-selection-feedback={true}
                    class-hover={element.hoverFeedback}
                    class-selected={element.selected}
                />
            );
            if (!vnode.children) {
                vnode.children = [];
            }
            vnode.children.push(feedback);
        }
        return vnode;
    }

    postUpdate(): void {
        // nothing to do
    }
}
