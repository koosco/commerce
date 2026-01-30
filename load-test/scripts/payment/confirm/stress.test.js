import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';

/**
 * Stress Test - Payment Confirm
 *
 * 목적: 고부하 환경에서 결제 확인 시스템 한계 및 안정성 검증
 * - 대량의 동시 결제 요청 처리 능력 테스트
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
    http_req_failed: ['rate<0.5'], // 결제 시뮬레이션이므로 높은 실패율 허용
    successful_payments: ['count>=0'],
    error_rate: ['rate<0.1'],
  },
};

const BASE_URL = config.paymentService;
const API_PATH = config.paths.payments;

// 커스텀 메트릭
const successfulPayments = new Counter('successful_payments');
const actualErrors = new Counter('actual_errors');
const paymentLatency = new Trend('payment_latency');
const errorRate = new Rate('error_rate');

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/confirm`);
  const payload = JSON.stringify({
    paymentKey: `test_payment_key_${Date.now()}_${__VU}_${__ITER}`,
    orderId: `test_order_${Date.now()}_${__VU}_${__ITER}`,
    amount: 29900,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: '10s',
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  if (res.status !== 0) {
    paymentLatency.add(res.timings.duration);
  }

  // 기본 검증
  check(res, {
    'status is 200 or 400': (r) => r.status === 200 || r.status === 400,
    'not timeout': (r) => r.status !== 0,
    'response time < 5s': (r) => r.timings.duration < 5000,
  });

  // 성공 응답 처리 및 검증
  if (res.status === 200) {
    successfulPayments.add(1);
    errorRate.add(false);

    check(res, {
      'success response valid': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          console.error(`Parse error on success: ${e.message}`);
          return false;
        }
      },
    });
  } else if (res.status === 400) {
    // 테스트 결제 키로 인한 실패는 정상
    errorRate.add(false);
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
    title: 'Stress Test - Payment Confirm',
    theme: 'stress',
  });

  return {
    'results/payment/confirm/stress.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
