import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { buildUrl } from '../../../lib/http.js';
import { login, authHeaders } from '../../../lib/auth.js';
import { fetchSkuIds, getRandomItem } from '../../../lib/dataLoader.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const AUTH_URL = config.authService;
const BASE_URL = config.orderService;
const API_PATH = config.paths.orders;

// Test credentials
const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

export function setup() {
  const token = login(AUTH_URL, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    console.error('Failed to get auth token during setup');
  }

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. create-order tests may fail.');
  }

  return { token, skuIds };
}

export default function (data) {
  if (!data.token) {
    console.error('No auth token available');
    return;
  }

  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(1);
    return;
  }

  const skuId = getRandomItem(data.skuIds);
  const url = buildUrl(BASE_URL, API_PATH);

  const payload = JSON.stringify({
    items: [
      {
        skuId: skuId,
        quantity: 1,
        unitPrice: 10000,
      },
    ],
  });

  const res = http.post(url, payload, {
    headers: authHeaders(data.token),
  });

  check(res, {
    'create-order: status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'create-order: response time < 1s': (r) => r.timings.duration < 1000,
    'create-order: has response body': (r) => r.body && r.body.length > 0,
    'create-order: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'create-order: has order id': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && (body.data.orderId || body.data.id);
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Order Service - Create Order Smoke Test',
    theme: 'smoke',
  });

  return {
    [resultPath('results/order/create-order/smoke.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
