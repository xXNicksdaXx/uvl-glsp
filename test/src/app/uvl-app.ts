/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { expect, GLSPSemanticApp } from '@eclipse-glsp/glsp-playwright';

import { UVLGraph } from '../graph/uvl-graph';
import { UVLToolPalette } from "../feature/tool-palette/uvl-tool-palette";

export class UVLApp extends GLSPSemanticApp {
    override readonly graph: UVLGraph;
    override readonly toolPalette: UVLToolPalette;

    protected override createGraph(): UVLGraph {
        return new UVLGraph({ locator: UVLGraph.locate(this) });
    }

    protected override createToolPalette(): UVLToolPalette {
        return new UVLToolPalette({ locator: UVLToolPalette.locate(this) });
    }

    /**
     * Wait for the application to be ready.
     * The server can take some time to send the data.
     */
    async waitForReady(): Promise<void> {
        await expect(this.locate().getByText('Push')).toBeVisible();
    }
}
