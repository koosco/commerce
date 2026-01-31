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

const BASE_URL = config.userService;
const API_PATH = config.paths.users;

// Test credentials
const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 인증 토큰 획득
export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }
  return { token };
}

export default function (data) {
  const url = buildUrl(BASE_URL, `${API_PATH}/me`);
  const payload = JSON.stringify({
    name: `Updated User ${Date.now()}`,
  });

  const res = http.patch(url, payload, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${data.token}`,
    },
  });

  check(res, {
    'update-me: status is 200': (r) => r.status === 200,
    'update-me: response time < 1s': (r) => r.timings.duration < 1000,
    'update-me: has response body': (r) => r.body && r.body.length > 0,
    'update-me: success is true': (r) => {
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
    title: 'User Service - Update Me Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/user/update-me/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
