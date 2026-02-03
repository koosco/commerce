import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { loginMultipleUsers } from '../../../lib/auth.js';
import { testUsers, getTokenForVu, getSkuForVu, fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Baseline Test - Get Stock
 *
 * 목적: 정상 부하 환경에서 재고 조회 성능 측정
 * - 일반적인 트래픽 패턴 시뮬레이션
 * - 성능 기준선(Baseline) 측정
 */

// Breaking Point: > 800 VU → Baseline: 520 VU (0.65 * B)
const BASELINE_VU = 520;

export const options = {
  stages: [
    { duration: '1m', target: Math.round(BASELINE_VU * 0.4) },  // warm-up: 208
    { duration: '1m', target: BASELINE_VU },                     // ramp: 520
    { duration: '5m', target: BASELINE_VU },                     // hold: 520
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    successful_requests: ['count>0'],
  },
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;

// 커스텀 메트릭
const successfulRequests = new Counter('successful_requests');
const requestLatency = new Trend('request_latency');

export function setup() {
  const tokens = loginMultipleUsers(config.authService, testUsers);

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, tokens[0], 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Baseline tests will fail.');
  }

  return { tokens, skuIds };
}

export default function (data) {
  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(1);
    return;
  }

  const token = getTokenForVu(data.tokens, __VU);
  const skuId = getSkuForVu(data.skuIds, __VU);
  const url = `${BASE_URL}${API_PATH}/${skuId}`;

  const res = http.get(url, {
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
      'has stock data': (r) => {
        try {
          const body = JSON.parse(r.body);
          return (
            body.data &&
            body.data.skuId &&
            typeof body.data.availableStock === 'number'
          );
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
    title: 'Baseline Test - Get Stock',
    theme: 'baseline',
  });

  return {
    [resultPath('results/inventory/get-stock/baseline.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
