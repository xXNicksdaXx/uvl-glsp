const js = require('@eslint/js');
const tseslint = require('typescript-eslint');
const headerPluginRaw = require('eslint-plugin-header');

const headerRule = headerPluginRaw.rules && headerPluginRaw.rules.header ? headerPluginRaw.rules.header : null;
const headerPlugin = headerRule
    ? {
          ...headerPluginRaw,
          rules: {
              ...headerPluginRaw.rules,
              header: {
                  ...headerRule,
                  meta: {
                      ...(headerRule.meta || {}),
                      // eslint-plugin-header does not define schema; ESLint 9 defaults to zero options.
                      schema: [{}, {}, {}, {}]
                  }
              }
          }
      }
    : headerPluginRaw;

module.exports = [
    {
        ignores: [
            '**/node_modules/**',
            '**/lib/**',
            '**/.eslintrc.js',
            '**/webpack.config.js',
            '**/webpack.prod.js',
            '**/dist/**',
            '**/build/**'
        ]
    },
    js.configs.recommended,
    ...tseslint.configs.recommended,
    {
        files: ['**/*.ts', '**/*.tsx'],
        plugins: {
            header: headerPlugin
        },
        rules: {
            'header/header': [
                'error',
                'block',
                [
                    '***************************************************************************',
                    ' *',
                    ' * Copyright © 2026 Nick Ruider. All rights reserved.',
                    ' * This work is licensed under the terms of the MIT license.',
                    ' * For a copy, see <https://opensource.org/licenses/MIT>.',
                    ' *',
                    ' ***************************************************************************'
                ]
            ]
        }
    }
];
