/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { resolveCliArgsFromVSCodeExecutablePath } from "@vscode/test-electron";
import * as cp from 'child_process';

export function installVsixViaCli(vscodeExecutablePath: string, vsixId: string, vsixPath: string): void {
    const [cli, ...defaultArgs] = resolveCliArgsFromVSCodeExecutablePath(vscodeExecutablePath);

    // On Windows, invoke .cmd files through cmd.exe so Node spawnSync can execute them reliably.
    cp.spawnSync('cmd.exe', ['/c', cli, ...defaultArgs, '--uninstall-extension', vsixId], {
        encoding: 'utf-8',
        stdio: 'inherit'
    });

    const result = cp.spawnSync('cmd.exe', ['/c', cli, ...defaultArgs, '--install-extension', vsixPath], {
        encoding: 'utf-8',
        stdio: 'inherit'
    });

    if (result.status !== 0) {
        const reason = result.error?.message ?? `exit code: ${result.status ?? 'unknown'}`;
        throw new Error(`[Extension] Extension install failed (${reason})`);
    }
}