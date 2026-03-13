/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { resolveCliArgsFromVSCodeExecutablePath } from "@vscode/test-electron";
import * as cp from 'child_process';

function formatSpawnError(result: cp.SpawnSyncReturns<string>, command: string, args: string[]): string {
    const fragments = [
        `command: ${command} ${args.join(' ')}`,
        `status: ${result.status ?? 'null'}`,
        `signal: ${result.signal ?? 'null'}`
    ];

    if (result.error?.message) {
        fragments.push(`error: ${result.error.message}`);
    }
    if (result.stdout?.trim()) {
        fragments.push(`stdout: ${result.stdout.trim()}`);
    }
    if (result.stderr?.trim()) {
        fragments.push(`stderr: ${result.stderr.trim()}`);
    }

    return fragments.join(' | ');
}

function runVsCodeCli(cli: string, args: string[]): cp.SpawnSyncReturns<string> {
    return cp.spawnSync(cli, args, {
        encoding: 'utf-8',
        stdio: 'pipe',
        shell: process.platform === 'win32'
    });
}

export function installVsixViaCli(vscodeExecutablePath: string, vsixId: string, vsixPath: string): void {
    const [cli, ...defaultArgs] = resolveCliArgsFromVSCodeExecutablePath(vscodeExecutablePath);

    const uninstallArgs = [...defaultArgs, '--uninstall-extension', vsixId];
    const uninstallResult = runVsCodeCli(cli, uninstallArgs);

    if (uninstallResult.status !== 0 && uninstallResult.status !== null) {
        console.warn(`[Extension] Fallback uninstall returned non-zero exit code. ${formatSpawnError(uninstallResult, cli, uninstallArgs)}`);
    }

    const installArgs = [...defaultArgs, '--install-extension', vsixPath];
    const result = runVsCodeCli(cli, installArgs);

    if (result.status !== 0) {
        throw new Error(`[Extension] Extension install failed (${formatSpawnError(result, cli, installArgs)})`);
    }
}