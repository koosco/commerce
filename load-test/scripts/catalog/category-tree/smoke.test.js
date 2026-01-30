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

const BASE_URL = config.catalogService;
const API_PATH = config.paths.categories;

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/tree`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'category-tree: status is 200': (r) => r.status === 200,
    'category-tree: response time < 1s': (r) => r.timings.duration < 1000,
    'category-tree: has response body': (r) => r.body && r.body.length > 0,
    'category-tree: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'category-tree: has data array': (r) => {
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
    title: 'Catalog Service - Category Tree Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/catalog/category-tree/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
