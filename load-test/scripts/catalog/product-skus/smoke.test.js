import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
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

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  // Dynamically fetch a valid product ID from product list
  const listUrl = buildUrl(BASE_URL, `${API_PATH}?page=0&size=1`);
  const res = http.get(listUrl, {
    headers: { Authorization: `Bearer ${token}` },
  });

  let productId = null;
  try {
    const body = JSON.parse(res.body);
    if (body.success && body.data && body.data.content && body.data.content.length > 0) {
      productId = body.data.content[0].productId;
    }
  } catch (e) {
    console.warn(`Failed to parse product list response: ${e.message}`);
  }

  if (!productId) {
    console.warn('No products found in catalog. Product SKU tests will be skipped.');
  }

  return { token, productId };
}

export default function (data) {
  if (!data.productId) {
    console.warn('Skipping: no productId available');
    sleep(1);
    return;
  }

  const url = buildUrl(BASE_URL, `${API_PATH}/${data.productId}/skus`);

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
    [resultPath('results/catalog/product-skus/smoke.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
