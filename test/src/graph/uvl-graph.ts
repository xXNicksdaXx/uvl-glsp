/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    GLSPSemanticGraph,
    isPNodeConstructor,
    PModelElement,
    PModelElementConstructor,
    waitForFunction
} from '@eclipse-glsp/glsp-playwright';

export class UVLGraph extends GLSPSemanticGraph {
    override async waitForCreationOfType<TElement extends PModelElement>(
        constructor: PModelElementConstructor<TElement>,
        creator: () => Promise<void>
    ): Promise<TElement[]> {
        const elements = await super.waitForCreationOfType(constructor, creator);

        if (isPNodeConstructor(constructor)) {
            await waitForFunction(async () =>
                elements.every(element => element.locate().evaluate(node => node?.classList.contains('selected')))
            );
        }

        return elements as unknown as TElement[];
    }
}
