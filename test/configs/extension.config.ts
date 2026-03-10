/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import * as path from "path";

export const VSCODE_VERSION = '1.88.1';
export const REPOSITORY_ROOT = path.join(__dirname, '..', '..');

export const UVL_VSCODE_EXTENSION: VSCodeExtensionConfig = {
    id: 'nickruider.uvl-vscode-extension',
    fileName: 'uvl-vscode.vsix',
    path: path.join('client', 'vscode', 'extension')
}

interface VSCodeExtensionConfig {
    id: string;
    fileName: string;
    path: string;
}