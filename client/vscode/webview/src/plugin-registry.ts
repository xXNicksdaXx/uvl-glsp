/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import { ContainerConfiguration } from "@eclipse-glsp/client";
import { bpDiagramModule } from "uvl-bp-sprotty";

declare const __UVL_CONTAINER_MODULE_IDS__: readonly string[];

const webviewPluginModules: Record<string, ContainerConfiguration> = {
    'uvl-core': [],
    'uvl-bp': [bpDiagramModule]
};

export function resolveWebviewPluginModules(): ContainerConfiguration {
    const enabledPluginIds = __UVL_CONTAINER_MODULE_IDS__.length > 0
        ? __UVL_CONTAINER_MODULE_IDS__
        : ['uvl-core'];

    for (const pluginId of enabledPluginIds) {
        if (!webviewPluginModules[pluginId]) {
            throw new Error(`Unknown webview module id '${pluginId}' configured for build profile.`);
        }
    }

    return enabledPluginIds.flatMap(pluginId => webviewPluginModules[pluginId] ?? []);
}
