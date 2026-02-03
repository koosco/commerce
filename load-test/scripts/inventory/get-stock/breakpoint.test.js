import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { loginMultipleUsers } from '../../../lib/auth.js';
import { testUsers, getTokenForVu, getSkuForVu, fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Breakpoint Test - Get Stock
 *
 * 목적: 재고 조회의 최대 지속 가능 VU (Breaking Point) 탐색
 * - 기존 stress max(500) 이상으로 점진적 증가
 * - p(95) > 1000ms 또는 error rate > 5% 지점 식별
 */

export const options = {
  stages: [
    { duration: '1m', target: 200 },
    { duration: '2m', target: 500 },
    { duration: '2m', target: 500 },
    { duration: '2m', target: 800 },
    { duration: '2m', target: 800 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    successful_requests: ['count>0'],
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
    console.warn('No SKU IDs found. Tests will fail.');
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

  if (res.status !== 0) {
    requestLatency.add(res.timings.duration);
  }

  check(res, {
    'status is 200': (r) => r.status === 200,
    'not timeout': (r) => r.status !== 0,
    'response time < 2s': (r) => r.timings.duration < 2000,
  });

  if (res.status === 200) {
    successfulRequests.add(1);
    errorRate.add(false);

    check(res, {
      'success response valid': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
    });
  } else {
    actualErrors.add(1);
    errorRate.add(true);
  }

  sleep(0.5);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: 'Breakpoint Test - Get Stock',
    theme: 'stress',
  });

  return {
    [resultPath('results/inventory/get-stock/breakpoint.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
