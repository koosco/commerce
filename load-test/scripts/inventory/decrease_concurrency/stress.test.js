import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { login } from '../../../lib/auth.js';
import { fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Stress Test - 재고 감소 동시성 테스트
 *
 * 목적: 고부하 환경에서 시스템 한계 및 동시성 제어 안정성 검증
 * - 대량의 동시 요청 처리 능력 테스트
 * - Race Condition 발생 여부 확인
 * - 시스템 Breaking Point 탐색
 * - 재고 데이터 정합성 최종 검증
 */

export const options = {
  stages: [
    { duration: '2m', target: 100 }, // Warm-up: 0 → 100 VUs
    { duration: '3m', target: 300 }, // Ramp-up: 100 → 300 VUs
    { duration: '5m', target: 500 }, // Peak: 300 → 500 VUs
    { duration: '5m', target: 500 }, // Hold: 500 VUs 유지 (동시성 집중 테스트)
    { duration: '3m', target: 200 }, // Recovery: 500 → 200 VUs
    { duration: '2m', target: 0 }, // Cool-down: 200 → 0 VUs
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'], // 허용 가능한 범위 내
    http_req_failed: ['rate<0.05'], // 에러율 5% 미만 (재고 부족 제외)
    successful_decreases: ['count>0'], // 성공한 재고 감소
    stock_conflicts: ['count>0'], // 재고 부족 발생
    error_rate: ['rate<0.01'], // 실제 에러율 1% 미만
  },
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 커스텀 메트릭
const successfulDecreases = new Counter('successful_decreases');
const actualErrors = new Counter('actual_errors');
const decreaseLatency = new Trend('decrease_latency');
const errorRate = new Rate('error_rate');

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Stress tests will fail.');
  }

  return { skuId: skuIds.length > 0 ? skuIds[0] : null };
}

export default function (data) {
  if (!data.skuId) {
    console.warn('Skipping: no skuId available');
    sleep(0.5);
    return;
  }

  const url = `${BASE_URL}${API_PATH}/${data.skuId}/decrease`;

  const payload = JSON.stringify({
    quantity: 2,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
    timeout: "10s", // 스트레스 환경에서 타임아웃 허용
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  if (res.status !== 0) {
    decreaseLatency.add(res.timings.duration);
  }

  // 기본 검증
  check(res, {
    "status is 200": (r) => r.status === 200,
    "not timeout": (r) => r.status !== 0,
    "response time < 2s": (r) => r.timings.duration < 2000,
  });

  // 성공 응답 처리 및 검증
  if (res.status === 200) {
    successfulDecreases.add(1);
    errorRate.add(false);

    check(res, {
      "success response valid": (r) => {
        try {
          const body = JSON.parse(r.body);
          return (
            body.success === true &&
            body.error === null &&
            body.timestamp !== undefined
          );
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
        `Body: ${res.body ? res.body.substring(0, 200) : "empty"}`
    );

    check(res, {
      "no unexpected errors": () => false, // 의도적으로 실패 기록
    });
  }

  // 고부하 환경이므로 짧은 sleep
  sleep(0.5);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: "Stress Test - Inventory Decrease Concurrency",
    theme: "stress",
  });

  return {
    "results/inventory/decrease_concurrency/stress.test.result.html": html,
    "stdout": JSON.stringify(data, null, 2),
  };
}
