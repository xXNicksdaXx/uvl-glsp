/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { Marker, expect, test } from '@eclipse-glsp/glsp-playwright';

import { UVLApp } from "../../src/app/uvl-app";
import { UVLGraph } from "../../src/graph/uvl-graph";
import { UVLToolPalette } from "../../src/feature/tool-palette/uvl-tool-palette";

test.describe('The tool palette', () => {
    let app: UVLApp;
    let graph: UVLGraph;
    let toolPalette: UVLToolPalette;

    test.beforeEach(async ({ integration }) => {
        app = new UVLApp({
            type: 'integration',
            integration
        });
        await app.waitForReady();
        graph = app.graph;
        toolPalette = app.toolPalette;
    });

    test('should allow to access the content items', async () => {
        await toolPalette.waitForVisible();

        const groups = await toolPalette.content.toolGroups();
        expect(groups.length).toBe(3);
        expect(await groups[0].header()).toBe('Elements');
        expect(await groups[1].header()).toBe('Relations');
        expect(await groups[2].header()).toBe('Constraints');

        const elements = await groups[0].items();
        expect(elements.length).toBe(3);
        expect(await elements[0].text()).toBe('Attribute');

        const relations = await groups[1].items();
        expect(relations.length).toBe(5);
        expect(await relations[0].text()).toBe('Alternative');

        const constraints = await groups[2].items();
        expect(constraints.length).toBe(2);
        expect(await constraints[0].text()).toBe('Equivalence');

        const headerGroup = await toolPalette.content.toolGroupByHeaderText('Relations');
        expect(await headerGroup.header()).toBe('Relations');

        const headerElements = await headerGroup.items();
        expect(headerElements.length).toBe(5);
        expect(await headerElements[0].text()).toBe('Alternative');
        expect(await headerElements[1].text()).toBe('Group Cardinality');

        const toolElement = await toolPalette.content.toolElement('Elements', 'Feature');
        expect(await toolElement.text()).toBe('Feature');
    });

    test('should allow to access the toolbar items', async () => {
        await toolPalette.waitForVisible();

        const deleteTool = toolPalette.toolbar.deletionTool();
        await expect(deleteTool).not.toContainClass('clicked');
        await deleteTool.click();

        const selectionTool = toolPalette.toolbar.selectionTool();
        await expect(selectionTool).not.toContainClass('clicked');
        await selectionTool.click();

        const marqueeTool = toolPalette.toolbar.marqueeTool();
        await expect(marqueeTool).not.toContainClass('clicked');
        await marqueeTool.click();

        const searchTool = toolPalette.toolbar.searchTool();
        expect(searchTool.input.isHidden()).toBeTruthy();

        await searchTool.click();
        expect(searchTool.input.isVisible()).toBeTruthy();
        await searchTool.search('Auto');
    });

    test('should allow to validate', async () => {
        const markers = await graph.waitForCreationOfType(Marker, async () => {
            await toolPalette.toolbar.validateTool().click();
        });

        expect(markers.length).toBeGreaterThan(0);
    });

    test.afterEach(async ({ integration }) => {
        await integration?.close();
    });
});