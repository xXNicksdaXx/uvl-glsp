// @ts-check
const path = require('path');
const webpack = require('webpack');
const { resolveBuildProfile } = require('../build-profile');

const outputPath = path.resolve(__dirname, './dist/');

/**@type {import('webpack').Configuration}*/
module.exports = env => {
    const buildProfile = resolveBuildProfile(env?.profile);

    return {
        target: 'web',

        entry: path.resolve(__dirname, 'src/index.ts'),
        output: {
            filename: 'webview.js',
            path: outputPath
        },
        devtool: 'eval-source-map',
        mode: 'development',

        resolve: {
            fallback: {
                fs: false,
                net: false
            },
            alias: {
                process: 'process/browser'
            },
            extensions: ['.ts', '.tsx', '.js']
        },
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: ['ts-loader']
                },
                {
                    test: /\.js$/,
                    use: ['source-map-loader'],
                    enforce: 'pre'
                },
                {
                    test: /\.css$/,
                    exclude: /\.useable\.css$/,
                    use: ['style-loader', 'css-loader']
                }
            ]
        },
        plugins: [
            new webpack.DefinePlugin({
                __UVL_BUILD_PROFILE_ID__: JSON.stringify(buildProfile.id),
                __UVL_CONTAINER_MODULE_IDS__: JSON.stringify(buildProfile.containerModuleIds || [])
            })
        ],
        ignoreWarnings: [/Failed to parse source map/, /Can't resolve .* in '.*ws\/lib'/],
        performance: {
            hints: false
        }
    };
};
