import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';
import { buildUrl } from '../../../lib/http.js';

/**
 * Baseline Test - User Registration
 *
 * 목적: 정상 부하 환경에서 회원가입 성능 측정
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
    successful_registrations: ['count>0'],
  },
};

const BASE_URL = config.userService;
const API_PATH = config.paths.users;

// 커스텀 메트릭
const successfulRegistrations = new Counter('successful_registrations');
const registrationLatency = new Trend('registration_latency');

export default function () {
  const url = buildUrl(BASE_URL, API_PATH);
  const timestamp = Date.now();
  const uniqueEmail = `baseline-user-${timestamp}-${__VU}-${__ITER}@example.com`;

  const payload = JSON.stringify({
    email: uniqueEmail,
    password: 'Test@1234',
    name: `Baseline User ${__VU}`,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  registrationLatency.add(res.timings.duration);

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
    successfulRegistrations.add(1);

    check(res, {
      'success field is true': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      'has userId': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && body.data.userId;
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
    title: 'Baseline Test - User Registration',
    theme: 'baseline',
  });

  return {
    'results/user/registration/baseline.test.result.html': html,
    stdout: JSON.stringify(data, null, 2),
  };
}
