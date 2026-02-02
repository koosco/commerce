import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { login } from '../../../lib/auth.js';
import { fetchSkuIds, getRandomItem } from '../../../lib/dataLoader.js';

/**
 * Baseline Test - Create Order
 *
 * 목적: 정상 부하 환경에서 주문 생성 성능 측정
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
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.01'],
    successful_orders: ['count>0'],
  },
};

const BASE_URL = config.orderService;
const API_PATH = config.paths.orders;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 커스텀 메트릭
const successfulOrders = new Counter('successful_orders');
const orderLatency = new Trend('order_latency');

export function setup() {
  const token = login(config.authService, TEST_EMAIL, TEST_PASSWORD);
  if (!token) {
    throw new Error('Failed to obtain auth token in setup');
  }

  const skuIds = fetchSkuIds(config.catalogService, config.paths.products, token, 5);
  if (skuIds.length === 0) {
    console.warn('No SKU IDs found. Baseline tests may fail.');
  }

  return { token, skuIds };
}

export default function (data) {
  if (!data.skuIds || data.skuIds.length === 0) {
    console.warn('Skipping: no skuIds available');
    sleep(1);
    return;
  }

  const url = buildUrl(BASE_URL, API_PATH);
  const payload = JSON.stringify({
    items: [
      {
        skuId: getRandomItem(data.skuIds),
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
      Authorization: `Bearer ${data.token}`,
    },
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  orderLatency.add(res.timings.duration);

  // 기본 검증
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 1s': (r) => r.timings.duration < 1000,
    'content-type is json': (r) =>
      r.headers['Content-Type'] &&
      r.headers['Content-Type'].includes('application/json'),
  });

  // 성공 응답 처리
  if (res.status === 200) {
    successfulOrders.add(1);

    check(res, {
      'success field is true': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      'has orderId': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && body.data.orderId;
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
    title: 'Baseline Test - Create Order',
    theme: 'baseline',
  });

  return {
    'results/order/create-order/baseline.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
