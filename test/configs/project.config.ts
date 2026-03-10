/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { GLSPPlaywrightOptions, VSCodeIntegrationOptions } from '@eclipse-glsp/glsp-playwright';
import { PlaywrightTestOptions, PlaywrightWorkerOptions, Project } from '@playwright/test';
import * as path from 'path';

import {REPOSITORY_ROOT, UVL_VSCODE_EXTENSION} from './extension.config';

export function createVSCodeProject(): Project<PlaywrightTestOptions & GLSPPlaywrightOptions, PlaywrightWorkerOptions>[] {
    const vscodeIntegrationOptions: VSCodeIntegrationOptions = {
        type: 'VSCode',
        vsixId: UVL_VSCODE_EXTENSION.id,
        vsixPath: path.join(__dirname, '..', 'playwright', UVL_VSCODE_EXTENSION.fileName),
        workspace: path.join(REPOSITORY_ROOT, 'client', 'workspace'),
        file: 'test.uvl',
        storagePath: path.join(__dirname, '..', 'playwright', '.storage', 'vscode.setup.json'),
    };

    return [
        {
            name: 'vscode-setup',
            timeout: 5 * 60 * 1000,
            testMatch: ['setup/vscode.setup.ts'],
            use: {
                integrationOptions: vscodeIntegrationOptions
            }
        },
        {
            name: 'vscode',
            timeout: 60 * 1000,
            testMatch: ['**/*.spec.ts'],
            dependencies: ['vscode-setup'],
            use: {
                integrationOptions: vscodeIntegrationOptions
            }
        }
    ];
}
