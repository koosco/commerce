import http from "k6/http";
import { check, sleep } from "k6";
import { config } from "../../../config/local.js";
import { generateHTMLReport } from "../../utils/htmlReporter.js";

/**
 * Smoke Test - 재고 감소 동시성 테스트
 *
 * 목적: 기본 기능이 정상적으로 작동하는지 최소 부하로 검증
 * - API 연결 확인
 * - 기본적인 재고 감소 동작 검증
 * - 응답 형식 확인
 */

export const options = {
  vus: 2,
  duration: "30s",
  thresholds: {
    http_req_duration: ["p(95)<1000"], // 95%의 요청이 1초 이내
    http_req_failed: ["rate<0.1"],     // 에러율 10% 미만
  },
};

const SKU_ID = "00008217-b1ae-4045-9500-2d4b9fffaa32";
const BASE_URL = config.inventoryService;

export default function () {
  const url = `${BASE_URL}/api/inventories/${SKU_ID}/decrease`;

  const payload = JSON.stringify({
    quantity: 2,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  // 기본 검증
  check(res, {
    "status is 200": (r) => r.status === 200,
    "response time < 1s": (r) => r.timings.duration < 1000,
    "response has body": (r) => r.body.length > 0,
  });

  // 성공 응답 검증
  if (res.status === 200) {
    check(res, {
      "success is true": (r) => {
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
    title: "Smoke Test - Inventory Decrease Concurrency",
    theme: "smoke",
  });

  return {
    "results/inventory/decrease_concurrency/smoke.test.result.html": html,
    "stdout": JSON.stringify(data, null, 2),
  };
}
