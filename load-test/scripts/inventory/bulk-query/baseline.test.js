import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { login } from '../../../lib/auth.js';
import { fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Baseline Test - Bulk Query
 *
 * 목적: 정상 부하 환경에서 재고 일괄 조회 성능 측정
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

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

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

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Baseline tests will fail.');
  }

  return { skuIds };
}

export default function (data) {
  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(1);
    return;
  }

  const url = `${BASE_URL}${API_PATH}/bulk`;
  const payload = JSON.stringify({
    skuIds: data.skuIds,
  });

  const res = http.post(url, payload, {
    headers: {
      'Content-Type': 'application/json',
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
      'has inventories array': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && Array.isArray(body.data.inventories);
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
    title: 'Baseline Test - Bulk Query',
    theme: 'baseline',
  });

  return {
    'results/inventory/bulk-query/baseline.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
