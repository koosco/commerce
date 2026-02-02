import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
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

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 2);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. bulk-query tests will fail.');
  }

  return { skuIds };
}

export default function (data) {
  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(1);
    return;
  }

  const url = `${BASE_URL}${API_PATH}/bulk`;
  const payload = JSON.stringify({
    skuIds: data.skuIds,
  });

  const res = http.post(url, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'bulk-query: status is 200': (r) => r.status === 200,
    'bulk-query: response time < 1s': (r) => r.timings.duration < 1000,
    'bulk-query: has response body': (r) => r.body && r.body.length > 0,
    'bulk-query: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'bulk-query: has inventories array': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && Array.isArray(body.data.inventories);
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Inventory Service - Bulk Query Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/inventory/bulk-query/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
