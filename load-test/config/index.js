import { localConfig } from './local.js';
import { prodConfig } from './prod.js';

const ENV = __ENV.ENV || 'local';

const configs = {
  local: localConfig,
  prod: prodConfig,
};

export const config = configs[ENV];
