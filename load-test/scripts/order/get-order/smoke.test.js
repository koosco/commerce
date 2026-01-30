import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { buildUrl } from '../../../lib/http.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const BASE_URL = config.orderService;
const API_PATH = config.paths.orders;

// Test order ID (should exist in the system)
const TEST_ORDER_ID = 1;

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/${TEST_ORDER_ID}`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'get-order: status is 200': (r) => r.status === 200,
    'get-order: response time < 1s': (r) => r.timings.duration < 1000,
    'get-order: has response body': (r) => r.body && r.body.length > 0,
    'get-order: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'get-order: has orderId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.orderId;
      } catch {
        return false;
      }
    },
    'get-order: has status': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.status;
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Order Service - Get Order Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/order/get-order/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
