import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';

export const options = {
  vus: 1,
  duration: '5s',
};

const BASE_URL = config.inventoryService;

export default function () {
  const res = http.get(`${BASE_URL}/actuator/health`);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
