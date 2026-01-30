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

const BASE_URL = config.userService;
const API_PATH = config.paths.users;

// Test user ID (should exist in the system)
const TEST_USER_ID = 1;

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/${TEST_USER_ID}`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'get-user: status is 200': (r) => r.status === 200,
    'get-user: response time < 1s': (r) => r.timings.duration < 1000,
    'get-user: has response body': (r) => r.body && r.body.length > 0,
    'get-user: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'get-user: has userId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.userId;
      } catch {
        return false;
      }
    },
    'get-user: has email': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.email;
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'User Service - Get User Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/user/get-user/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
