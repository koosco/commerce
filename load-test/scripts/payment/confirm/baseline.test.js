import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';
import { loginMultipleUsers } from '../../../lib/auth.js';
import { testUsers, getTokenForVu } from '../../../lib/dataLoader.js';

/**
 * Baseline Test - Payment Confirm
 *
 * 목적: 정상 부하 환경에서 결제 확인 성능 측정
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
    http_req_failed: ['rate<0.5'], // 결제 시뮬레이션이므로 높은 실패율 허용
    successful_payments: ['count>=0'],
  },
};

const BASE_URL = config.paymentService;
const API_PATH = config.paths.payments;

// 커스텀 메트릭
const successfulPayments = new Counter('successful_payments');
const paymentLatency = new Trend('payment_latency');

export function setup() {
  const tokens = loginMultipleUsers(config.authService, testUsers);
  return { tokens };
}

export default function (data) {
  const token = getTokenForVu(data.tokens, __VU);
  const url = buildUrl(BASE_URL, `${API_PATH}/confirm`);
  const payload = JSON.stringify({
    paymentKey: `test_payment_key_${Date.now()}_${__VU}_${__ITER}`,
    orderId: `test_order_${Date.now()}_${__VU}_${__ITER}`,
    amount: 29900,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  paymentLatency.add(res.timings.duration);

  // 기본 검증
  check(res, {
    'status is 200 or 400': (r) => r.status === 200 || r.status === 400,
    'response time < 1s': (r) => r.timings.duration < 1000,
    'content-type is json': (r) =>
      r.headers['Content-Type'] &&
      r.headers['Content-Type'].includes('application/json'),
  });

  // 성공 응답 처리
  if (res.status === 200) {
    successfulPayments.add(1);

    check(res, {
      'success field is true': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
    });
  } else if (res.status === 400) {
    // 테스트 결제 키로 인한 실패는 정상
    check(res, {
      'has error message': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.error !== null;
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
    title: 'Baseline Test - Payment Confirm',
    theme: 'baseline',
  });

  return {
    [resultPath('results/payment/confirm/baseline.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
