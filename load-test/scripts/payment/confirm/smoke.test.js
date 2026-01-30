import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { smokeThresholds } from '../../../lib/thresholds.js';
import { buildUrl } from '../../../lib/http.js';
import { login, authHeaders } from '../../../lib/auth.js';

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: {
    ...smokeThresholds,
    // Payment endpoint may return 4xx for invalid payment keys, which is expected
    http_req_failed: ['rate<0.5'],
  },
};

const AUTH_URL = config.authService;
const BASE_URL = config.paymentService;
const API_PATH = config.paths.payments;

// Test credentials
const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

export function setup() {
  const token = login(AUTH_URL, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    console.error('Failed to get auth token during setup');
  }
  return { token };
}

export default function (data) {
  if (!data.token) {
    console.error('No auth token available');
    return;
  }

  // Test payment confirmation endpoint with a test payment key
  // This will likely return an error (invalid payment), but we're testing connectivity
  const url = buildUrl(BASE_URL, `${API_PATH}/confirm`);

  const payload = JSON.stringify({
    paymentKey: `test_payment_key_${__VU}_${__ITER}`,
    orderId: `test_order_${__VU}_${__ITER}`,
    amount: 10000,
  });

  const res = http.post(url, payload, {
    headers: authHeaders(data.token),
  });

  // For smoke test, we check that the endpoint responds (even with error)
  // Valid responses: 200 (success), 400/404 (invalid payment - expected), 401 (auth issue)
  check(res, {
    'payment-confirm: endpoint responds': (r) => r.status > 0,
    'payment-confirm: response time < 2s': (r) => r.timings.duration < 2000,
    'payment-confirm: has response body': (r) => r.body && r.body.length > 0,
    'payment-confirm: is JSON response': (r) => {
      try {
        JSON.parse(r.body);
        return true;
      } catch {
        return false;
      }
    },
    'payment-confirm: not server error (5xx)': (r) => r.status < 500,
  });

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Payment Service - Confirm Smoke Test',
    theme: 'smoke',
  });

  return {
    'results/payment/confirm/smoke.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
