/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { Action, RequestAction, type ResponseAction } from '@eclipse-glsp/protocol';

export interface HighlightElementAction extends RequestAction<HighlightElementActionResponse> {
    kind: typeof HighlightElementAction.KIND;
    elementIds: string[];
    isHighlighted: boolean;
}

export const HighlightElementAction = {
    KIND: 'highlightElement',

    is(obj: unknown): obj is HighlightElementAction {
        return RequestAction.hasKind(obj, HighlightElementAction.KIND);
    },

    create(
        opts: { elementIds: string[]; isHighlighted: boolean } | { elementId: string; isHighlighted: boolean }
    ): HighlightElementAction {
        const elementIds = 'elementIds' in opts ? opts.elementIds : [opts.elementId];
        return {
            kind: HighlightElementAction.KIND,
            requestId: '',
            elementIds,
            isHighlighted: opts.isHighlighted
        };
    }
} as const;

export interface HighlightElementActionResponse extends ResponseAction {
    kind: typeof HighlightElementActionResponse.KIND;
    ok: boolean;
}

export const HighlightElementActionResponse = {
    KIND: 'highlightElementResponse',

    is(obj: unknown): obj is HighlightElementActionResponse {
        return Action.hasKind(obj, HighlightElementActionResponse.KIND);
    },

    create(opts?: { ok?: boolean; responseId?: string }): HighlightElementActionResponse {
        return {
            kind: HighlightElementActionResponse.KIND,
            responseId: opts?.responseId ?? '',
            ok: opts?.ok ?? true
        };
    }
} as const;
