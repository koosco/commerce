import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport, resultPath } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';

/**
 * Baseline Test - Auth Login
 *
 * 목적: 정상 부하 환경에서 로그인 성능 측정
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
    successful_logins: ['count>0'],
  },
};

const BASE_URL = config.authService;
const API_PATH = config.paths.auth;

const TEST_EMAIL = 'loadtest1@example.com';
const TEST_PASSWORD = 'Test@1234';

// 커스텀 메트릭
const successfulLogins = new Counter('successful_logins');
const loginLatency = new Trend('login_latency');

export default function () {
  const url = buildUrl(BASE_URL, `${API_PATH}/login`);
  const payload = JSON.stringify({
    email: TEST_EMAIL,
    password: TEST_PASSWORD,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  loginLatency.add(res.timings.duration);

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
    successfulLogins.add(1);

    check(res, {
      'success field is true': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      'has accessToken': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && body.data.accessToken;
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
    title: 'Baseline Test - Auth Login',
    theme: 'baseline',
  });

  return {
    [resultPath('results/auth/login/baseline.test.result.html')]: html,
    stdout: JSON.stringify(data, null, 2),
  };
}
