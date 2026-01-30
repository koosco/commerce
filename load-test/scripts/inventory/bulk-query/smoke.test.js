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

// Test SKU IDs (should exist in the system)
const TEST_SKU_IDS = [
  '00008217-b1ae-4045-9500-2d4b9fffaa32',
  '00008217-b1ae-4045-9500-2d4b9fffaa33',
];

export default function () {
  const url = `${BASE_URL}${API_PATH}/bulk`;
  const payload = JSON.stringify({
    skuIds: TEST_SKU_IDS,
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
    'bulk-query: has data array': (r) => {
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
    title: 'Inventory Service - Bulk Query Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/inventory/bulk-query/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
