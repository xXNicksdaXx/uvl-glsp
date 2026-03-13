/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { IntegrationOptions, VSCodeIntegrationOptions, VSCodeStorage, expect, setup } from '@eclipse-glsp/glsp-playwright';
import { REPOSITORY_ROOT, UVL_VSCODE_EXTENSION, VSCODE_VERSION } from "../../configs/extension.config";
import * as fs from "fs";
import * as path from "path";

import { installVsixViaCli } from "./install-vsix";

setup.describe.configure({
    mode: 'serial'
});

function assertVSCodeOptions(options?: IntegrationOptions): asserts options is VSCodeIntegrationOptions {
    if (options === undefined || options.type !== 'VSCode') {
        throw new Error('This setup can only be executed by VS Code integrations');
    }
}

setup.describe('Setup VSCode', () => {
    setup('Download VSCode', async ({ vscodeSetup, integrationOptions }) => {
        assertVSCodeOptions(integrationOptions);
        expect(vscodeSetup).toBeDefined();

        const vscodeExecutablePath = await vscodeSetup!.downloadVSCode(VSCODE_VERSION);

        await VSCodeStorage.write(integrationOptions.storagePath, { vscodeExecutablePath });
    });

    setup('Copy UVL extension', async () => {
        // copy the extension to the playwright folder, so it can be installed in the next step
        const sourcePath = path.join(REPOSITORY_ROOT, UVL_VSCODE_EXTENSION.path, UVL_VSCODE_EXTENSION.fileName);
        const destinationPath = path.join(REPOSITORY_ROOT, 'test', 'playwright', UVL_VSCODE_EXTENSION.fileName);

        if (!fs.existsSync(sourcePath)) {
            throw new Error(`Extension file not found at ${sourcePath}. Please build the extension before running the tests.`);
        }
        fs.copyFileSync(sourcePath, destinationPath);
    });

    setup('Install extension', async ({ vscodeSetup, integrationOptions }) => {
        assertVSCodeOptions(integrationOptions);
        expect(vscodeSetup).toBeDefined();

        const vscodeExecutablePath = (await VSCodeStorage.read(integrationOptions.storagePath)).vscodeExecutablePath;
        try {
            await vscodeSetup!.install({vscodeExecutablePath});
        } catch (error: unknown) {
            const baseInstallMessage = error instanceof Error
                ? `${error.message}${error.stack ? `\n${error.stack}` : ''}`
                : `Unknown error: ${String(error)}`;

            console.error(`[Extension] Default installation failed. Falling back to CLI install.\n${baseInstallMessage}`);

            try {
                installVsixViaCli(vscodeExecutablePath, integrationOptions.vsixId, integrationOptions.vsixPath);
            } catch (fallbackError: unknown) {
                const fallbackMessage = fallbackError instanceof Error
                    ? `${fallbackError.message}${fallbackError.stack ? `\n${fallbackError.stack}` : ''}`
                    : `Unknown fallback error: ${String(fallbackError)}`;

                throw new Error(
                    `[Extension] Both default and fallback installation failed.\nDefault failure:\n${baseInstallMessage}\nFallback failure:\n${fallbackMessage}`
                );
            }
        }
    });
});