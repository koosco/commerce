import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../../../config/index.js';
import { generateHTMLReport } from '../../utils/htmlReporter.js';

/**
 * Baseline Test - 재고 감소 동시성 테스트
 *
 * 목적: 정상 부하 환경에서 시스템 성능 측정 및 동시성 제어 검증
 * - 일반적인 트래픽 패턴 시뮬레이션
 * - 성능 기준선(Baseline) 측정
 * - 데이터 정합성 검증
 */

export const options = {
  stages: [
    { duration: '1m', target: 20 }, // ramp
    { duration: '1m', target: 50 }, // ramp
    { duration: '5m', target: 50 }, // hold (baseline)
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95%는 500ms, 99%는 1s 이내
    http_req_failed: ['rate<0.01'], // 에러율 1% 미만 (재고 부족 제외)
    successful_decreases: ['count>0'], // 성공한 재고 감소 건수
  },
};

const BASE_URL = config.inventoryService;
const API_PATH = config.paths.inventory;
const SKU_ID = '00008217-b1ae-4045-9500-2d4b9fffaa32';

// 커스텀 메트릭
const successfulDecreases = new Counter('successful_decreases');
const decreaseLatency = new Trend('decrease_latency');

export default function () {
  const url = `${BASE_URL}${API_PATH}/${SKU_ID}/decrease`;

  const payload = JSON.stringify({
    quantity: 2,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  // 응답 시간 기록
  decreaseLatency.add(res.timings.duration);

  // 기본 검증
  check(res, {
    "status is 200": (r) => r.status === 200,
    "response time < 500ms": (r) => r.timings.duration < 500,
    "content-type is json": (r) =>
      r.headers["Content-Type"] &&
      r.headers["Content-Type"].includes("application/json"),
  });

  // 성공 응답 처리
  if (res.status === 200) {
    successfulDecreases.add(1);

    check(res, {
      "success field is true": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.success === true;
        } catch (e) {
          return false;
        }
      },
      "has timestamp": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.timestamp !== undefined;
        } catch (e) {
          return false;
        }
      },
    });
  } else {
    // 에러 발생 시 로깅
    console.error(
      `Unexpected status: ${res.status}, body: ${res.body.substring(0, 200)}`
    );
  }

  sleep(1);
}

export function handleSummary(data) {
  const html = generateHTMLReport(data, {
    title: "Baseline Test - Inventory Decrease Concurrency",
    theme: "baseline",
  });

  return {
    "results/inventory/decrease_concurrency/baseline.test.result.html": html,
    "stdout": JSON.stringify(data, null, 2),
  };
}
