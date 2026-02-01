import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { generateUniqueEmail } from '../../../lib/dataLoader.js';

/**
 * Smoke Test - User Registration
 *
 * Purpose: Verify basic registration functionality under minimal load
 * - API connection check
 * - Registration endpoint validation
 * - Response format verification
 */

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<2000', 'avg<1000'],
    http_req_failed: ['rate<0.1'],
    checks: ['rate>0.9'],
  },
};

const BASE_URL = config.userService;
const API_PATH = config.paths.users;

export default function () {
  // Generate unique email for each request to avoid conflicts
  const uniqueEmail = generateUniqueEmail(__VU, __ITER);

  const url = buildUrl(BASE_URL, API_PATH);
  const payload = JSON.stringify({
    email: uniqueEmail,
    password: 'Test@1234',
    name: `Load Test User VU${__VU}_${__ITER}_${Date.now()}`,
  });

  const res = http.post(url, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  check(res, {
    'register: status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'register: response time < 1s': (r) => r.timings.duration < 1000,
    'register: has response body': (r) => r.body && r.body.length > 0,
    'register: success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'register: no error in response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return !body.error;
      } catch {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'User Service - Registration Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/user/registration/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
