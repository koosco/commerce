import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

// Test SKU ID (should exist in the system)
const TEST_SKU_ID = '00008217-b1ae-4045-9500-2d4b9fffaa32';

export default function () {
  const url = `${BASE_URL}${API_PATH}/${TEST_SKU_ID}/increase`;
  const payload = JSON.stringify({
    quantity: 10,
  });

  const res = http.post(url, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'increase-stock: status is 200': (r) => r.status === 200,
    'increase-stock: response time < 1s': (r) => r.timings.duration < 1000,
    'increase-stock: has response body': (r) => r.body && r.body.length > 0,
    'increase-stock: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Inventory Service - Increase Stock Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/inventory/increase-stock/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
