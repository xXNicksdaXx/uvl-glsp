/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import 'reflect-metadata';

import {
    configureDefaultCommands,
    GlspSocketServerLauncher,
    GlspVscodeConnector,
    SocketGlspVscodeServer
} from '@eclipse-glsp/vscode-integration/node';
import * as path from 'path';
import * as process from 'process';
import * as vscode from 'vscode';
import { configureBuildProfileCommandContributions } from './plugin-registry';
import UVLEditorProvider from "./uvl-editor-provider";

declare const __UVL_BUILD_PROFILE_ID__: string;
declare const __UVL_SERVER_JAR_NAME__: string;

export const LOG_DIR = path.join(__dirname, '..', 'logs');

const DEFAULT_SERVER_PORT = '0';

export async function activate(context: vscode.ExtensionContext): Promise<void> {
    await vscode.commands.executeCommand('setContext', 'uvl.buildProfile', __UVL_BUILD_PROFILE_ID__);

    // Start server process using quickstart component
    let serverProcess: GlspSocketServerLauncher | undefined;
    const port = JSON.parse(process.env.GLSP_SERVER_PORT || DEFAULT_SERVER_PORT);

    if (process.env.GLSP_SERVER_DEBUG !== 'true') {
        const modulePath = vscode.Uri.joinPath(context.extensionUri, 'dist', __UVL_SERVER_JAR_NAME__).fsPath;
        serverProcess = new GlspSocketServerLauncher({
            executable: modulePath,
            socketConnectionOptions: { port: port },
            additionalArgs: [
                '--consoleLog', 'true',
                '--fileLog', 'false',
                '--logDir', LOG_DIR
            ],
            logging: true
        });
        context.subscriptions.push(serverProcess);
        await serverProcess.start();
    }

    // Wrap server with quickstart component
    const minimalServer = new SocketGlspVscodeServer({
        clientId: 'glsp.uvl',
        clientName: 'universal-variability-language',
        connectionOptions: { port: serverProcess?.getPort() || port }
    });

    // Initialize GLSP-VSCode connector with server wrapper
    const glspVscodeConnector = new GlspVscodeConnector({
        server: minimalServer,
        logging: true
    });

    const customEditorProvider = vscode.window.registerCustomEditorProvider(
        'uvl.glspDiagram',
        new UVLEditorProvider(context, glspVscodeConnector),
        {
            webviewOptions: { retainContextWhenHidden: true },
            supportsMultipleEditorsPerDocument: false
        }
    );

    context.subscriptions.push(minimalServer, glspVscodeConnector, customEditorProvider);
    await minimalServer.start();

    configureDefaultCommands({ extensionContext: context, connector: glspVscodeConnector, diagramPrefix: 'uvl' });
    configureBuildProfileCommandContributions({ extensionContext: context, connector: glspVscodeConnector, diagramPrefix: 'uvl' });
}
