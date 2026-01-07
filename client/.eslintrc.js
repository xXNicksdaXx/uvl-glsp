/** @type {import('eslint').Linter.Config} */
module.exports = {
    extends: '@eclipse-glsp',
    ignorePatterns: ['**/{node_modules,lib}', '**/.eslintrc.js', '**/webpack.config.js', '**/webpack.prod.js'],

    root: true,
    parserOptions: {
        tsconfigRootDir: __dirname,
        project: 'tsconfig.eslint.json'
    },
    plugins: ['header'],
    rules: {
        'header/header': [
            2,
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
};
