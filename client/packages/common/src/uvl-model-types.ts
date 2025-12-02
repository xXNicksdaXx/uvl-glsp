/****************************************************************************
 *
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/

import { DefaultTypes } from '@eclipse-glsp/client';

/**
 * Defines string constants for all element types used in UVL diagrams.
 * <p>
 * For corresponding server-side types, see <code>UVLModelTypes.java</code>.
 */
export namespace UVLModelTypes {
    export const FEATURE = `${DefaultTypes.NODE}:feature`;
    export const CONSTRAINT = `${DefaultTypes.NODE}:constraint`;

    export const B_THREAD = `${DefaultTypes.LABEL}:b-thread`;

    export const MANDATORY = `${DefaultTypes.EDGE}:mandatory`;
    export const OPTIONAL = `${DefaultTypes.EDGE}:optional`;
    export const ALTERNATIVE = `${DefaultTypes.EDGE}:alternative`;
}
