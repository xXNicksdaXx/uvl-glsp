'use strict';

const path = require('path');
const webpack = require('webpack');
const CopyPlugin = require('copy-webpack-plugin');
const { resolveBuildProfile } = require('../build-profile');

/**@type {import('webpack').Configuration}*/
module.exports = env => {
    const buildProfile = resolveBuildProfile(env?.profile);

    return {
        target: 'node',

        entry: path.resolve(__dirname, 'src/uvl-extension.ts'),
        output: {
            path: path.resolve(__dirname, 'dist'),
            filename: 'uvl-extension.js',
            libraryTarget: 'commonjs2'
        },
        devtool: 'source-map',
        externals: {
            vscode: 'commonjs vscode'
        },
        mode: 'development',
        resolve: {
            extensions: ['.ts', '.js']
        },
        module: {
            rules: [
                {
                    test: /\.ts$/,
                    exclude: /node_modules/,
                    use: [
                        {
                            loader: 'ts-loader'
                        }
                    ]
                },
                {
                    test: /\.css$/,
                    use: [
                        'style-loader',
                        'css-loader'
                    ]
                }
            ]
        },
        plugins: [
            new webpack.DefinePlugin({
                __UVL_BUILD_PROFILE_ID__: JSON.stringify(buildProfile.id),
                __UVL_CONTAINER_MODULE_IDS__: JSON.stringify(buildProfile.containerModuleIds || []),
                __UVL_COMMAND_CONTRIBUTION_IDS__: JSON.stringify(buildProfile.commandContributionIds || []),
                __UVL_SERVER_JAR_NAME__: JSON.stringify('uvl-server.glsp.jar')
            }),
            new CopyPlugin({
                patterns: [
                    {
                        from: path.resolve(__dirname, '..', 'webview', 'dist'),
                        to: path.resolve(__dirname, 'dist')
                    },
                    {
                        from: buildProfile.serverJarAbsolutePath,
                        to: path.resolve(__dirname, 'dist', 'uvl-server.glsp.jar')
                    }
                ]
            })
        ],
        ignoreWarnings: [/Can't resolve .* in '.*ws\/lib'/],
        performance: {
            hints: false
        }
    };
};
