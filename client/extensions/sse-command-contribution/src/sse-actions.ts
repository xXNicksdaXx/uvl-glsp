/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { Action } from '@eclipse-glsp/protocol';

export interface SSEStartListeningAction extends Action {
    kind: typeof SSEStartListeningAction.KIND;
}

export const SSEStartListeningAction = {
    KIND: 'sseStartListening',

    is(obj: unknown): obj is SSEStartListeningAction {
        return Action.hasKind(obj, SSEStartListeningAction.KIND);
    },

    create(): SSEStartListeningAction {
        return {
            kind: SSEStartListeningAction.KIND,
        };
    }
} as const;

export interface SSEHighFrequencyPollingAction extends Action {
    kind: typeof SSEHighFrequencyPollingAction.KIND;
}

export const SSEHighFrequencyPollingAction = {
    KIND: 'sseHighFrequencyPolling',

    is(obj: unknown): obj is SSEHighFrequencyPollingAction {
        return Action.hasKind(obj, SSEHighFrequencyPollingAction.KIND);
    },

    create(): SSEHighFrequencyPollingAction {
        return {
            kind: SSEHighFrequencyPollingAction.KIND,
        };
    }
} as const;

export interface SSEStopListeningAction extends Action {
    kind: typeof SSEStopListeningAction.KIND;
}

export const SSEStopListeningAction = {
    KIND: 'sseStopListening',

    is(obj: unknown): obj is SSEStopListeningAction {
        return Action.hasKind(obj, SSEStopListeningAction.KIND);
    },

    create(): SSEStopListeningAction {
        return {
            kind: SSEStopListeningAction.KIND,
        };
    }
} as const;
