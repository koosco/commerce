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
const API_PATH = config.paths.products;

export default function () {
  // Get product list with pagination
  const url = buildUrl(BASE_URL, `${API_PATH}?page=0&size=10`);

  const res = http.get(url, {
    headers: {
      Accept: 'application/json',
    },
  });

  check(res, {
    'product-list: status is 200': (r) => r.status === 200,
    'product-list: response time < 1s': (r) => r.timings.duration < 1000,
    'product-list: has response body': (r) => r.body && r.body.length > 0,
    'product-list: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'product-list: has content array': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && Array.isArray(body.data.content);
      } catch {
        return false;
      }
    },
    'product-list: has pagination info': (r) => {
      try {
        const body = JSON.parse(r.body);
        return (
          body.data &&
          typeof body.data.totalElements === 'number' &&
          typeof body.data.totalPages === 'number'
        );
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Catalog Service - Product List Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/catalog/product-list/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
