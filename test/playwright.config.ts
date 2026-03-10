/****************************************************************************
 *
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 *
 ****************************************************************************/
import 'reflect-metadata';

import type { GLSPPlaywrightOptions } from '@eclipse-glsp/glsp-playwright';
import { type PlaywrightTestConfig } from '@playwright/test';

import { createVSCodeProject } from "./configs/project.config";

/**
 * See https://playwright.dev/docs/test-configuration.
 */
const config: PlaywrightTestConfig<GLSPPlaywrightOptions> = {
  testDir: 'tests',
  expect: {
    timeout: 5000
  },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: process.env.CI ? [['html', { open: 'never' }], ['@estruyf/github-actions-reporter']] : [['html', { open: 'never' }]],
  use: {
    actionTimeout: 0,
    trace: 'on-first-retry'
  },
  projects: createVSCodeProject(),
};

export default config;
