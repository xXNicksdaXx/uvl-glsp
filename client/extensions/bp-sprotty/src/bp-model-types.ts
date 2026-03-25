/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { DefaultTypes } from '@eclipse-glsp/client';

/**
 * Defines string constants for all element types used in UVL BP diagrams.
 * <p>
 * For corresponding server-side types, see <code>BPModelTypes.java</code>.
 */
export namespace BPModelTypes {
    export const BP_ENV = `${DefaultTypes.NODE}:env`;
    export const BP_CONFIG = `${DefaultTypes.NODE}:config`;

    export const B_THREAD = `${DefaultTypes.NODE}:b-thread`;

    export const REQUESTED_EVENT = `${DefaultTypes.NODE}:requested-event`;
    export const BLOCKED_EVENT = `${DefaultTypes.NODE}:blocked-event`;
    export const WAITED_FOR_EVENT = `${DefaultTypes.NODE}:waited-for-event`;

    export const REQUESTED  = `${DefaultTypes.EDGE}:requested`;
    export const BLOCKED     = `${DefaultTypes.EDGE}:blocked`;
    export const WAITED_FOR  = `${DefaultTypes.EDGE}:waited-for`;
    export const SELECTED    = `${DefaultTypes.EDGE}:selected`;
    export const CONFLICTING = `${DefaultTypes.EDGE}:conflicting`;

}


