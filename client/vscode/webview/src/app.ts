/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import '@eclipse-glsp/vscode-integration-webview/css/glsp-vscode.css';
import 'reflect-metadata';

import { ContainerConfiguration } from '@eclipse-glsp/client';
import { GLSPStarter } from '@eclipse-glsp/vscode-integration-webview';
import { Container } from 'inversify';
import { initializeUvlDiagramContainer } from 'uvl-sprotty';

class UvlDiagramStarter extends GLSPStarter {
    createContainer(...containerConfiguration: ContainerConfiguration): Container {
        return initializeUvlDiagramContainer(new Container(), ...containerConfiguration);
    }
}

export function launch(): void {
     new UvlDiagramStarter();
}
