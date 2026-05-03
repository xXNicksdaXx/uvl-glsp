/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { CommandContext } from '@eclipse-glsp/vscode-integration';
import * as vscode from 'vscode';

import {
    SSEStartListeningAction,
    SSEHighFrequencyPollingAction,
    SSEStopListeningAction
} from './sse-actions';

export function configureSSECommandContributions(context: CommandContext): void {
    const { extensionContext, diagramPrefix, connector } = context;

    extensionContext.subscriptions.push(
        vscode.commands.registerCommand(`${diagramPrefix}.sse.startListening`, () => {
            connector.dispatchAction(SSEStartListeningAction.create());
        }),
        vscode.commands.registerCommand(`${diagramPrefix}.sse.highFrequencyPolling`, () => {
            connector.dispatchAction(SSEHighFrequencyPollingAction.create());
        }),
        vscode.commands.registerCommand(`${diagramPrefix}.sse.stopListening`, () => {
            connector.dispatchAction(SSEStopListeningAction.create());
        })
    );
}
