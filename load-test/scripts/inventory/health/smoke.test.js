import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';

const ENV = __ENV.ENV || 'local';

export const options = {
  vus: 1,
  duration: ENV === 'prod' ? '1s' : '5s',
};

const BASE_URL = config.inventoryService;

export default function () {
  if (ENV === 'prod') {
    console.log('Skipping inventory health check in prod (actuator not exposed via ingress)');
    return;
  }
  const res = http.get(`${BASE_URL}/actuator/health`);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
