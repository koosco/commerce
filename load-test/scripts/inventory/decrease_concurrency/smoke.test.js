import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { login } from '../../../lib/auth.js';
import { fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Smoke Test - 재고 감소 동시성 테스트
 *
 * 목적: 기본 기능이 정상적으로 작동하는지 최소 부하로 검증
 * - API 연결 확인
 * - 기본적인 재고 감소 동작 검증
 * - 응답 형식 확인
 */

export const options = {
  vus: 2,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95%의 요청이 1초 이내
    http_req_failed: ['rate<0.1'], // 에러율 10% 미만
  },
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 1);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. decrease_concurrency tests will fail.');
  }

  return { skuId: skuIds.length > 0 ? skuIds[0] : null };
}

export default function (data) {
  if (!data.skuId) {
    console.warn('Skipping: no skuId available');
    sleep(1);
    return;
  }

  const url = `${BASE_URL}${API_PATH}/decrease`;

  const payload = JSON.stringify({
    items: [{ skuId: data.skuId, quantity: 2 }],
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  // 기본 검증
  check(res, {
    "status is 200": (r) => r.status === 200,
    "response time < 1s": (r) => r.timings.duration < 1000,
    "response has body": (r) => r.body.length > 0,
  });

  // 성공 응답 검증
  if (res.status === 200) {
    check(res, {
      "success is true": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      "has timestamp": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.timestamp !== undefined;
        } catch (e) {
          return false;
        }
      },
    });
  } else {
    // 에러 발생 시 로깅
    console.error(
      `Unexpected status: ${res.status}, body: ${res.body.substring(0, 200)}`
    );
  }

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: "Smoke Test - Inventory Decrease Concurrency",
    theme: "smoke",
  });

  return {
    [resultPath('results/inventory/decrease_concurrency/smoke.test.result.html')]: html,
    "stdout": JSON.stringify(data, null, 2),
  };
}
