/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { HighlightElementAction, HighlightElementActionResponse } from 'uvl-common';

import {
    Action,
    Command,
    type CommandExecutionContext,
    type CommandReturn,
    HandleActionResult,
    IActionHandler,
    ICommandStack,
    MaybePromise,
    TYPES
} from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';

const HIGHLIGHT_CLASS = 'highlight-glow';
const DEFAULT_HIGHLIGHT_TIMEOUT_MS = 2500;

const pendingUnhighlightTimeouts = new Map<string, ReturnType<typeof setTimeout>>();

@injectable()
export class HighlightElementsActionHandler implements IActionHandler {
    @inject(TYPES.ICommandStack)
    protected commandStack: ICommandStack;

    handle(action: Action): MaybePromise<HandleActionResult> {
        if (!HighlightElementAction.is(action)) {
            return undefined;
        }

        const command = new ApplyHighlightCommand(action.elementIds, action.isHighlighted);
        return this.commandStack
            .execute(command)
            .then(() =>
                action.requestId
                    ? HighlightElementActionResponse.create({ ok: true, responseId: action.requestId })
                    : undefined
            )
            .catch(() =>
                action.requestId
                    ? HighlightElementActionResponse.create({ ok: false, responseId: action.requestId })
                    : undefined
            );
    }
}

class ApplyHighlightCommand extends Command {
    constructor(
        protected readonly elementIds: ReadonlyArray<string>,
        protected readonly highlighted: boolean
    ) {
        super();
    }

    execute(context: CommandExecutionContext): CommandReturn {
        return applyHighlight(context, this.elementIds, this.highlighted);
    }

    undo(context: CommandExecutionContext): CommandReturn {
        return context.root;
    }

    redo(context: CommandExecutionContext): CommandReturn {
        return this.execute(context);
    }
}

function applyHighlight(
    context: CommandExecutionContext,
    elementIds: ReadonlyArray<string>,
    highlighted: boolean
): CommandReturn {
    let modelChanged = false;

    for (const rawId of elementIds) {
        const id = rawId?.trim();
        if (!id) {
            continue;
        }

        const element = context.root.index.getById(id);
        if (!element) {
            clearPendingUnhighlight(context, id);
            continue;
        }

        const nextClasses = toggleHighlightClass(element.cssClasses, highlighted);
        const previousClasses = element.cssClasses ?? [];
        const hasChanged =
            previousClasses.length !== nextClasses.length
            || previousClasses.some((cssClass, index) => cssClass !== nextClasses[index]);

        if (!hasChanged) {
            continue;
        }

        element.cssClasses = nextClasses;
        modelChanged = true;

        if (highlighted) {
            scheduleUnhighlight(context, id);
        } else {
            clearPendingUnhighlight(context, id);
        }
    }

    return { model: context.root, modelChanged };
}

function scheduleUnhighlight(context: CommandExecutionContext, elementId: string): void {
    clearPendingUnhighlight(context, elementId);
    const timerKey = getTimerKey(context, elementId);

    const timeoutHandle = setTimeout(() => {
        pendingUnhighlightTimeouts.delete(timerKey);

        const element = context.root.index.getById(elementId);
        if (!element) {
            return;
        }

        element.cssClasses = toggleHighlightClass(element.cssClasses, false);

        // Trigger a normal action roundtrip so the diagram refreshes consistently.
        const dispatcher = (context as unknown as {
            actionDispatcher?: { dispatch(action: HighlightElementAction): unknown };
        }).actionDispatcher;
        dispatcher?.dispatch(HighlightElementAction.create({ elementId, isHighlighted: false }));
    }, DEFAULT_HIGHLIGHT_TIMEOUT_MS);

    pendingUnhighlightTimeouts.set(timerKey, timeoutHandle);
}

function clearPendingUnhighlight(context: CommandExecutionContext, elementId: string): void {
    const timerKey = getTimerKey(context, elementId);
    const timeoutHandle = pendingUnhighlightTimeouts.get(timerKey);
    if (!timeoutHandle) {
        return;
    }

    clearTimeout(timeoutHandle);
    pendingUnhighlightTimeouts.delete(timerKey);
}

function getTimerKey(context: CommandExecutionContext, elementId: string): string {
    const rootId = (context.root as { id?: string }).id ?? 'root';
    return `${rootId}:${elementId}`;
}

function toggleHighlightClass(cssClasses: ReadonlyArray<string> | undefined, highlighted: boolean): string[] {
    const classSet = new Set(cssClasses ?? []);

    if (highlighted) {
        classSet.add(HIGHLIGHT_CLASS);
    } else {
        classSet.delete(HIGHLIGHT_CLASS);
    }

    return [...classSet];
}
