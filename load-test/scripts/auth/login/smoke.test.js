import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { buildUrl } from '../../../lib/http.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: smokeThresholds,
};

const BASE_URL = config.authService;
const API_PATH = config.paths.auth;

// Test credentials (should exist in the system)
const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/login`);
  const payload = JSON.stringify({
    email: TEST_EMAIL,
    password: TEST_PASSWORD,
  });

  const res = http.post(url, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'login: status is 200': (r) => r.status === 200,
    'login: response time < 1s': (r) => r.timings.duration < 1000,
    'login: has response body': (r) => r.body && r.body.length > 0,
    'login: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'login: has accessToken': (r) => {
      return r.headers['Authorization'] && r.headers['Authorization'].length > 0;
    },
    'login: has refreshToken cookie': (r) => {
      const setCookie = r.headers['Set-Cookie'] || '';
      return setCookie.includes('refreshToken=');
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Auth Service - Login Smoke Test',
    theme: 'smoke',
  });

  return {
    [resultPath('results/auth/login/smoke.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
