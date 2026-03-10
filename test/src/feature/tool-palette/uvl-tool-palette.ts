/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import {
    GLSPToolPalette,
    GLSPToolPaletteContent,
    GLSPToolPaletteOptions,
    ToolPaletteContentGroup,
    ToolPaletteContentItem
} from '@eclipse-glsp/glsp-playwright';

export class UVLToolPalette extends GLSPToolPalette {
    override readonly content: UVLToolPaletteContent;

    constructor(options: GLSPToolPaletteOptions) {
        super(options);
        this.content = new UVLToolPaletteContent(this);
    }
}

export interface UVLToolGroups {
    Elements: 'Feature' | 'Attribute' | 'Feature Cardinality';
    Relations: 'Mandatory' | 'Optional' | 'Or' | 'Alternative' | 'Group Cardinality';
    Constraints: 'Equivalence' | 'Implication';
}

export class UVLToolPaletteContent extends GLSPToolPaletteContent {
    async toolGroups(): Promise<ToolPaletteContentGroup[]> {
        return super.groupsOfType(ToolPaletteContentGroup);
    }

    async toolGroupByHeaderText<TToolGroupKey extends keyof UVLToolGroups>(
        headerText: TToolGroupKey
    ): Promise<ToolPaletteContentGroup> {
        return super.groupByHeaderText(headerText, ToolPaletteContentGroup);
    }

    async toolElement<TToolGroupKey extends keyof UVLToolGroups>(
        groupHeader: TToolGroupKey,
        elementText: UVLToolGroups[TToolGroupKey]
    ): Promise<ToolPaletteContentItem<ToolPaletteContentGroup>> {
        return super.itemBy({
            groupHeaderText: groupHeader,
            groupConstructor: ToolPaletteContentGroup,
            elementText,
            elementConstructor: ToolPaletteContentItem
        });
    }
}