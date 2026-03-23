/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { DefaultTypes } from '@eclipse-glsp/client';

/**
 * Defines string constants for all element types that are specific to the UVL BP extension.
 * <p>
 * These types are in addition to the base UVL types from the {@code uvl-common} package.
 */
export namespace BPModelTypes {
    export const BP_ENV = `${DefaultTypes.NODE}:env`;
    export const BP_CONFIG = `${DefaultTypes.NODE}:config`;
    export const BP_THREAD = `${DefaultTypes.NODE}:thread`;

    export const BP_REQUESTED  = `${DefaultTypes.EDGE}:bp-requested`;
    export const BP_BLOCKED     = `${DefaultTypes.EDGE}:bp-blocked`;
    export const BP_WAITED_FOR  = `${DefaultTypes.EDGE}:bp-waited-for`;
    export const BP_SELECTED    = `${DefaultTypes.EDGE}:bp-selected`;
    export const BP_CONFLICTING = `${DefaultTypes.EDGE}:bp-conflicting`;

}


