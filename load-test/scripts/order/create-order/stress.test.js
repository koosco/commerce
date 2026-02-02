import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { loginMultipleUsers } from '../../../lib/auth.js';
import { testUsers, getTokenForVu, getSkuForVu, fetchSkuIds } from '../../../lib/dataLoader.js';

/**
 * Stress Test - Create Order
 *
 * 목적: 고부하 환경에서 주문 생성 시스템 한계 및 안정성 검증
 * - 대량의 동시 주문 요청 처리 능력 테스트
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
    http_req_duration: ['p(95)<2000', 'p(99)<5000'],
    http_req_failed: ['rate<0.05'],
    successful_orders: ['count>0'],
    error_rate: ['rate<0.01'],
  },
};

const BASE_URL = config.orderService;
const API_PATH = config.paths.orders;

// 커스텀 메트릭
const successfulOrders = new Counter('successful_orders');
const actualErrors = new Counter('actual_errors');
const orderLatency = new Trend('order_latency');
const errorRate = new Rate('error_rate');

export function setup() {
  const tokens = loginMultipleUsers(config.authService, testUsers);

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, tokens[0], 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Stress tests may fail.');
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
  const url = buildUrl(BASE_URL, API_PATH);
  const payload = JSON.stringify({
    items: [
      {
        skuId,
        quantity: 1,
        unitPrice: 29900,
      },
    ],
    shippingAddress: {
      recipientName: 'Test User',
      phoneNumber: '010-1234-5678',
      postalCode: '12345',
      address: 'Test Address',
      detailAddress: 'Detail Address',
    },
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    timeout: '10s',
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  if (res.status !== 0) {
    orderLatency.add(res.timings.duration);
  }

  // 기본 검증
  check(res, {
    'status is 200': (r) => r.status === 200,
    'not timeout': (r) => r.status !== 0,
    'response time < 5s': (r) => r.timings.duration < 5000,
  });

  // 성공 응답 처리 및 검증
  if (res.status === 200) {
    successfulOrders.add(1);
    errorRate.add(false);

    check(res, {
      'success response valid': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true && body.data && body.data.orderId;
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
    title: 'Stress Test - Create Order',
    theme: 'stress',
  });

  return {
    [resultPath('results/order/create-order/stress.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
