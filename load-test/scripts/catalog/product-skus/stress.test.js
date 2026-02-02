import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { login } from '../../../lib/auth.js';

/**
 * Stress Test - Product SKUs
 *
 * 목적: 고부하 환경에서 상품 SKU 목록 조회 시스템 한계 및 안정성 검증
 * - 대량의 동시 조회 요청 처리 능력 테스트
 * - 시스템 Breaking Point 탐색
 */

export const options = {
  stages: [
    { duration: '2m', target: 100 },
    { duration: '3m', target: 300 },
    { duration: '5m', target: 500 },
    { duration: '5m', target: 500 },
    { duration: '3m', target: 200 },
    { duration: '2m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.05'],
    successful_requests: ['count>0'],
    error_rate: ['rate<0.01'],
  },
};

const BASE_URL = config.catalogService;
const API_PATH = config.paths.products;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 커스텀 메트릭
const successfulRequests = new Counter('successful_requests');
const actualErrors = new Counter('actual_errors');
const requestLatency = new Trend('request_latency');
const errorRate = new Rate('error_rate');

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  // 동적으로 상품 ID 조회
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
    sleep(0.5);
    return;
  }

  const url = buildUrl(BASE_URL, `${API_PATH}/${data.productId}/skus`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${data.token}`,
    },
    timeout: '10s',
  });

  // 응답 시간 기록
  if (res.status !== 0) {
    requestLatency.add(res.timings.duration);
  }

  // 기본 검증
  check(res, {
    'status is 200': (r) => r.status === 200,
    'not timeout': (r) => r.status !== 0,
    'response time < 2s': (r) => r.timings.duration < 2000,
  });

  // 성공 응답 처리 및 검증
  if (res.status === 200) {
    successfulRequests.add(1);
    errorRate.add(false);

    check(res, {
      'success response valid': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true && body.data && Array.isArray(body.data);
        } catch (e) {
          console.error(`Parse error on success: ${e.message}`);
          return false;
        }
      },
    });
  }
  // 에러 응답 처리
  else {
    actualErrors.add(1);
    errorRate.add(true);

    console.error(
      `[ERROR] Unexpected response - Status: ${res.status}, ` +
        `Duration: ${res.timings.duration}ms, ` +
        `Body: ${res.body ? res.body.substring(0, 200) : 'empty'}`
    );

    check(res, {
      'no unexpected errors': () => false,
    });
  }

  sleep(0.5);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Stress Test - Product SKUs',
    theme: 'stress',
  });

  return {
    'results/catalog/product-skus/stress.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
