import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { login } from '../../../lib/auth.js';
import { fetchSkuIds } from '../../../lib/dataLoader.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 1);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. get-stock tests will fail.');
  }

  return { skuId: skuIds.length > 0 ? skuIds[0] : null };
}

export default function (data) {
  if (!data.skuId) {
    console.warn('Skipping: no skuId available');
    sleep(1);
    return;
  }

  const url = `${BASE_URL}${API_PATH}/${data.skuId}`;

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'get-stock: status is 200': (r) => r.status === 200,
    'get-stock: response time < 1s': (r) => r.timings.duration < 1000,
    'get-stock: has response body': (r) => r.body && r.body.length > 0,
    'get-stock: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'get-stock: has skuId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.skuId;
      } catch {
        return false;
      }
    },
    'get-stock: has availableStock': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && typeof body.data.availableStock === 'number';
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Inventory Service - Get Stock Smoke Test',
    theme: 'smoke',
  });

  return {
    [resultPath('results/inventory/get-stock/smoke.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
