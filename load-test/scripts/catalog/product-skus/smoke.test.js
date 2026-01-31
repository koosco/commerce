import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { buildUrl } from '../../../lib/http.js';
import { login } from '../../../lib/auth.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const BASE_URL = config.catalogService;
const API_PATH = config.paths.products;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// Test product ID (should exist in the system)
const TEST_PRODUCT_ID = 1;

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }
  return { token };
}

export default function (data) {
  const url = buildUrl(BASE_URL, `${API_PATH}/${TEST_PRODUCT_ID}/skus`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${data.token}`,
    },
  });

  check(res, {
    'product-skus: status is 200': (r) => r.status === 200,
    'product-skus: response time < 1s': (r) => r.timings.duration < 1000,
    'product-skus: has response body': (r) => r.body && r.body.length > 0,
    'product-skus: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'product-skus: has data array': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && Array.isArray(body.data);
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Catalog Service - Product SKUs Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/catalog/product-skus/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
