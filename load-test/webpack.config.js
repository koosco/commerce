const path = require('path');

module.exports = {
  mode: 'production',
  entry: {
    smoke: './scripts/inventory/decrease_concurrency/smoke.test.js',
    baseline: './scripts/inventory/decrease_concurrency/baseline.test.js',
    stress: './scripts/inventory/decrease_concurrency/stress.test.js',
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    libraryTarget: 'commonjs',
    filename: '[name].bundle.js',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
        },
      },
    ],
  },
  target: 'web',
  externals: /^(k6|https?\:\/\/)(\/.*)?/,
  stats: {
    colors: true,
  },
  optimization: {
    minimize: false,
  },
};
