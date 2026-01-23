import http from "k6/http";
import { check, sleep } from "k6";
import { config } from "../../../config/local.js";

export const options = {
  vus: 1,
  duration: "5s",
};

export default function () {
  const res = http.get(`${config.inventoryService}/actuator/health`);

  check(res, {
    "status is 200": (r) => r.status === 200,
  });

  sleep(1);
}
