import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { loginMultipleUsers } from '../../../lib/auth.js';
import { testUsers, getTokenForVu, getSkuForVu, fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Stress Test - Get Stock
 *
 * 목적: 고부하 환경에서 재고 조회 시스템 한계 및 안정성 검증
 * - 대량의 동시 조회 요청 처리 능력 테스트
 * - 시스템 Breaking Point 탐색
 */

// Breaking Point: > 800 VU (p95 = 107ms at 800 VU, no degradation)
const B = 800;

export const options = {
  stages: [
    { duration: '2m', target: Math.round(B * 0.3) },   // warm-up: 240
    { duration: '3m', target: Math.round(B * 0.65) },  // ramp to baseline: 520
    { duration: '5m', target: Math.round(B * 0.65) },  // hold baseline: 520
    { duration: '3m', target: B },                      // push to breaking point: 800
    { duration: '5m', target: B },                      // hold at limit: 800
    { duration: '2m', target: 0 },                      // cooldown
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.05'],
    successful_requests: ['count>0'],
    error_rate: ['rate<0.01'],
  },
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

// 커스텀 메트릭
const successfulRequests = new Counter('successful_requests');
const actualErrors = new Counter('actual_errors');
const requestLatency = new Trend('request_latency');
const errorRate = new Rate('error_rate');

export function setup() {
  const tokens = loginMultipleUsers(config.authService, testUsers);

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, tokens[0], 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Stress tests will fail.');
  }

  return { tokens, skuIds };
}

export default function (data) {
  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(0.5);
    return;
  }

  const token = getTokenForVu(data.tokens, __VU);
  const skuId = getSkuForVu(data.skuIds, __VU);
  const url = `${BASE_URL}${API_PATH}/${skuId}`;

  const res = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
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
          return (
            body.success === true &&
            body.data &&
            body.data.skuId &&
            typeof body.data.availableStock === 'number'
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
    title: 'Stress Test - Get Stock',
    theme: 'stress',
  });

  return {
    [resultPath('results/inventory/get-stock/stress.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
