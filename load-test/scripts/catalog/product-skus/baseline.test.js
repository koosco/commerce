import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { login } from '../../../lib/auth.js';

/**
 * Baseline Test - Product SKUs
 *
 * 목적: 정상 부하 환경에서 상품 SKU 목록 조회 성능 측정
 * - 일반적인 트래픽 패턴 시뮬레이션
 * - 성능 기준선(Baseline) 측정
 */

export const options = {
  stages: [
    { duration: '1m', target: 20 },
    { duration: '1m', target: 50 },
    { duration: '5m', target: 50 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    successful_requests: ['count>0'],
  },
};

const BASE_URL = config.catalogService;
const API_PATH = config.paths.products;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 커스텀 메트릭
const successfulRequests = new Counter('successful_requests');
const requestLatency = new Trend('request_latency');

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
    sleep(1);
    return;
  }

  const url = buildUrl(BASE_URL, `${API_PATH}/${data.productId}/skus`);

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${data.token}`,
    },
  });

  // 응답 시간 기록
  requestLatency.add(res.timings.duration);

  // 기본 검증
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
    'content-type is json': (r) =>
      r.headers['Content-Type'] &&
      r.headers['Content-Type'].includes('application/json'),
  });

  // 성공 응답 처리
  if (res.status === 200) {
    successfulRequests.add(1);

    check(res, {
      'success field is true': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      'has data array': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && Array.isArray(body.data);
        } catch (e) {
          return false;
        }
      },
    });
  } else {
    console.error(
      `Unexpected status: ${res.status}, body: ${res.body.substring(0, 200)}`
    );
  }

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Baseline Test - Product SKUs',
    theme: 'baseline',
  });

  return {
    'results/catalog/product-skus/baseline.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
