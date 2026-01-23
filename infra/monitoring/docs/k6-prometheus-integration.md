# k6 → Prometheus 통합 가이드

k6 부하 테스트 메트릭을 Prometheus로 전송하여 Grafana에서 시각화하는 방법

---

## 🎯 개요

k6는 여러 출력 방식을 지원하지만, Prometheus 통합이 가장 실시간 모니터링에 적합합니다.

### 지원하는 출력 방식

| 방식 | 용도 | 실시간 | 권장도 |
|------|------|--------|--------|
| **Prometheus Remote Write** | 실시간 모니터링 | ✅ | ⭐⭐⭐ |
| **Prometheus Exporter** | 메트릭 pull | ✅ | ⭐⭐ |
| InfluxDB | 시계열 DB 저장 | ✅ | ⭐⭐ |
| JSON | 파일로 저장 | ❌ | ⭐ |
| k6 Cloud | 클라우드 서비스 | ✅ | ⭐⭐⭐ (유료) |

**이 문서는 Prometheus Remote Write 방식을 다룹니다.**

---

## 📦 사전 준비

### 1. k6 설치

```bash
# macOS (Homebrew)
brew install k6

# Linux (Debian/Ubuntu)
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
  --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | \
  sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Docker
docker pull grafana/k6:latest

# Windows (Chocolatey)
choco install k6
```

### 2. Prometheus 설정 확인

Prometheus에 Remote Write 수신 기능이 활성화되어 있어야 합니다.

**prometheus/prometheus.yml** 또는 **prometheus.local.yml**:

```yaml
# Remote Write 수신 설정 (이미 활성화되어 있음)
# k6가 보내는 메트릭을 저장하기 위한 설정
global:
  scrape_interval: 15s
  evaluation_interval: 15s

# k6 메트릭은 Remote Write로 전송되므로
# 별도 scrape 설정 불필요
```

---

## 🚀 빠른 시작 (5분 완성)

### 1. 간단한 k6 스크립트 작성

**test.js**:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // 0 → 20 VUs (30초)
    { duration: '1m', target: 20 },   // 20 VUs 유지 (1분)
    { duration: '30s', target: 0 },   // 20 → 0 VUs (30초)
  ],
};

export default function () {
  // Order Service 테스트
  const res = http.get('http://172.31.43.230:8083/actuator/health');

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(1);
}
```

### 2. Prometheus로 메트릭 전송하며 실행

```bash
k6 run \
  --out experimental-prometheus-rw \
  test.js
```

**기본 Prometheus URL**: `http://localhost:9090/api/v1/write`

### 3. Grafana에서 확인

1. http://localhost:3000 접속
2. Dashboards → Import
3. Dashboard ID: **2587** 입력 (k6 Prometheus)
4. Load → Import
5. 실시간 메트릭 확인! 🎉

---

## ⚙️ 고급 설정

### Prometheus URL 커스터마이징

```bash
# 다른 Prometheus 서버로 전송
K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
k6 run --out experimental-prometheus-rw test.js

# 환경변수로 설정
export K6_PROMETHEUS_RW_SERVER_URL=http://172.31.43.230:9090/api/v1/write
k6 run --out experimental-prometheus-rw test.js
```

### 메트릭에 태그(Label) 추가

```bash
# 환경별 구분
K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM=true \
K6_PROMETHEUS_RW_PUSH_INTERVAL=5s \
k6 run \
  --out experimental-prometheus-rw \
  --tag environment=staging \
  --tag service=order-service \
  test.js
```

**Prometheus에서 쿼리**:

```promql
# 특정 환경의 HTTP 요청만 조회
http_req_duration{environment="staging", service="order-service"}
```

### 여러 출력 동시 사용

```bash
# Prometheus + JSON 파일
k6 run \
  --out experimental-prometheus-rw \
  --out json=test-results.json \
  test.js
```

---

## 📊 실전 예제

### 예제 1: 모든 서비스 동시 부하 테스트

**load-test-all-services.js**:

```javascript
import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 50 },   // Ramp-up
    { duration: '3m', target: 50 },   // Stay
    { duration: '1m', target: 100 },  // Spike
    { duration: '2m', target: 100 },  // Stay
    { duration: '1m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95%가 500ms 이하
    http_req_failed: ['rate<0.01'],    // 에러율 1% 미만
  },
};

const BASE_URL = 'http://172.31.43.230';

const SERVICES = {
  auth: 8089,
  user: 8090,
  catalog: 8091,
  inventory: 8092,
  order: 8083,
  payment: 8094,
};

export default function () {
  // Auth Service
  group('Auth Service', () => {
    const res = http.get(`${BASE_URL}:${SERVICES.auth}/actuator/health`);
    check(res, {
      'auth is UP': (r) => r.status === 200,
      'auth response < 200ms': (r) => r.timings.duration < 200,
    });
  });

  // Order Service (주문 생성 시뮬레이션)
  group('Order Service', () => {
    const payload = JSON.stringify({
      userId: `user_${__VU}`,
      items: [
        { productId: 'prod_123', quantity: 2 },
      ],
    });

    const params = {
      headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(
      `${BASE_URL}:${SERVICES.order}/api/orders`,
      payload,
      params
    );

    check(res, {
      'order created': (r) => r.status === 201,
      'order response < 1s': (r) => r.timings.duration < 1000,
    });
  });

  sleep(1);
}
```

**실행**:

```bash
k6 run \
  --out experimental-prometheus-rw \
  --tag test=full-load \
  load-test-all-services.js
```

### 예제 2: 점진적 부하 증가 (Stress Test)

**stress-test.js**:

```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '2m', target: 100 },   // 정상 부하
    { duration: '5m', target: 100 },
    { duration: '2m', target: 200 },   // 부하 증가
    { duration: '5m', target: 200 },
    { duration: '2m', target: 300 },   // 한계 테스트
    { duration: '5m', target: 300 },
    { duration: '10m', target: 0 },    // 복구 시간 관찰
  ],
  thresholds: {
    http_req_duration: ['p(99)<3000'], // 99%가 3초 이하
  },
};

export default function () {
  const res = http.get('http://172.31.43.230:8083/api/orders');

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
```

### 예제 3: Spike Test (급격한 트래픽 증가)

**spike-test.js**:

```javascript
export const options = {
  stages: [
    { duration: '10s', target: 10 },    // 정상 상태
    { duration: '1m', target: 10 },
    { duration: '10s', target: 500 },   // 급격한 증가!
    { duration: '3m', target: 500 },    // 유지
    { duration: '10s', target: 10 },    // 정상으로 복귀
    { duration: '3m', target: 10 },
    { duration: '10s', target: 0 },
  ],
};
```

---

## 📈 Prometheus 메트릭 확인

### k6가 전송하는 주요 메트릭

| 메트릭명 | 설명 | 타입 |
|----------|------|------|
| `k6_http_reqs_total` | 총 HTTP 요청 수 | Counter |
| `k6_http_req_duration` | HTTP 요청 응답 시간 | Histogram |
| `k6_http_req_failed_total` | 실패한 요청 수 | Counter |
| `k6_vus` | 현재 Virtual Users 수 | Gauge |
| `k6_vus_max` | 최대 VUs | Gauge |
| `k6_iterations_total` | 총 반복 횟수 | Counter |
| `k6_data_sent` | 전송한 데이터량 | Counter |
| `k6_data_received` | 수신한 데이터량 | Counter |
| `k6_checks_total` | check() 총 실행 수 | Counter |
| `k6_checks_failed_total` | 실패한 check() 수 | Counter |

### Prometheus 쿼리 예시

```promql
# 초당 요청 수 (RPS)
rate(k6_http_reqs_total[1m])

# P95 응답 시간
histogram_quantile(0.95, rate(k6_http_req_duration_bucket[5m]))

# 에러율 (%)
(rate(k6_http_req_failed_total[1m]) / rate(k6_http_reqs_total[1m])) * 100

# 현재 VUs
k6_vus

# Check 성공률
(1 - (k6_checks_failed_total / k6_checks_total)) * 100
```

---

## 🎨 Grafana 대시보드

### 추천 대시보드

1. **k6 Prometheus** (ID: 2587) - 공식 대시보드
   - HTTP 메트릭, VUs, Checks 포함
   - 실시간 모니터링에 최적

2. **k6 Load Testing Results** (ID: 18030)
   - 시나리오별 분석
   - P95/P99 응답 시간

3. **커스텀 대시보드 만들기**

**grafana/dashboards/k6-custom.json** 예시:

```json
{
  "dashboard": {
    "title": "k6 Load Test - Commerce",
    "panels": [
      {
        "title": "Request Rate (RPS)",
        "targets": [
          {
            "expr": "rate(k6_http_reqs_total[1m])",
            "legendFormat": "RPS"
          }
        ]
      },
      {
        "title": "Response Time (P95)",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(k6_http_req_duration_bucket[5m]))",
            "legendFormat": "P95"
          }
        ]
      }
    ]
  }
}
```

---

## 🔧 Docker로 k6 실행

### Docker Compose 추가

**docker-compose.yml**:

```yaml
services:
  k6:
    image: grafana/k6:latest
    container_name: k6-loadtest
    networks:
      - monitoring
    volumes:
      - ./k6-scripts:/scripts
    command: run --out experimental-prometheus-rw /scripts/test.js
    environment:
      - K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write
      - K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM=true
```

### 실행

```bash
# 스크립트 폴더 생성
mkdir -p k6-scripts
cp test.js k6-scripts/

# Docker로 실행
docker-compose run --rm k6

# 또는 직접 실행
docker run --rm \
  --network monitoring_monitoring \
  -v $(pwd)/k6-scripts:/scripts \
  -e K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
  grafana/k6:latest \
  run --out experimental-prometheus-rw /scripts/test.js
```

---

## 🐛 트러블슈팅

### 문제 1: "connection refused" 에러

**증상**:
```
WARN[0000] Request Failed error="Post \"http://localhost:9090/api/v1/write\": dial tcp 127.0.0.1:9090: connect: connection refused"
```

**원인**: Prometheus가 실행 중이 아니거나 URL이 잘못됨

**해결**:
```bash
# 1. Prometheus 상태 확인
docker ps | grep prometheus

# 2. Prometheus URL 확인
curl http://localhost:9090/-/healthy

# 3. Docker 네트워크에서 실행 중이라면
K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
k6 run --out experimental-prometheus-rw test.js
```

### 문제 2: Grafana에서 메트릭이 안 보임

**원인**: k6 메트릭 이름 매핑 문제

**해결**:
```bash
# 1. Prometheus에서 메트릭 확인
curl http://localhost:9090/api/v1/label/__name__/values | grep k6

# 2. 메트릭이 있다면 Grafana 쿼리 수정
# 대시보드에서 Query inspector로 실제 메트릭명 확인

# 3. 타임 레인지 확인
# k6 실행 시간과 Grafana 시간 범위가 일치하는지 확인
```

### 문제 3: "experimental-prometheus-rw" 지원 안 됨

**증상**:
```
invalid output type 'experimental-prometheus-rw'
```

**원인**: k6 버전이 너무 낮음 (v0.34.0 이상 필요)

**해결**:
```bash
# k6 버전 확인
k6 version

# 업데이트
brew upgrade k6  # macOS
# 또는 최신 버전 재설치
```

### 문제 4: 메트릭이 너무 많아서 Prometheus가 느려짐

**해결**:
```bash
# 1. 메트릭 전송 간격 늘리기 (기본 1초 → 5초)
K6_PROMETHEUS_RW_PUSH_INTERVAL=5s \
k6 run --out experimental-prometheus-rw test.js

# 2. 불필요한 태그 제거
# k6 스크립트에서 discardResponseBodies 옵션 사용
export const options = {
  discardResponseBodies: true,  // 응답 본문 저장 안 함
};
```

---

## 📚 참고 자료

### 공식 문서
- [k6 공식 문서](https://k6.io/docs/)
- [k6 Prometheus Output](https://k6.io/docs/results-output/real-time/prometheus-remote-write/)
- [k6 Examples](https://k6.io/docs/examples/)

### k6 Cloud (선택)
무료 계정으로 더 많은 기능 사용 가능:
- 테스트 결과 저장
- 비교 분석
- 팀 협업

```bash
# k6 Cloud로 전송
k6 cloud test.js
```

### 성능 테스트 베스트 프랙티스

1. **점진적 부하 증가**: 갑작스러운 부하보다 단계적으로
2. **Ramp-down 포함**: 서비스 복구 시간 관찰
3. **Thresholds 설정**: 성능 목표 명확히
4. **태그 활용**: 환경별, 서비스별 구분
5. **정기적 실행**: CI/CD에 통합하여 회귀 방지

---

## ✅ 체크리스트

부하 테스트 전:

- [ ] Prometheus 실행 중 확인
- [ ] Grafana 대시보드 준비 (ID: 2587)
- [ ] k6 설치 확인 (v0.34.0+)
- [ ] 테스트 스크립트 작성
- [ ] 성능 목표(Thresholds) 정의
- [ ] 태그(환경, 서비스) 추가
- [ ] 테스트 대상 서비스 health 확인

부하 테스트 중:

- [ ] Grafana에서 실시간 모니터링
- [ ] 에러율 <1% 유지 확인
- [ ] 응답 시간 목표 달성 확인
- [ ] 서버 리소스(CPU, 메모리) 모니터링

부하 테스트 후:

- [ ] 결과 분석 및 문서화
- [ ] 병목 지점 식별
- [ ] 개선 사항 도출
- [ ] 다음 테스트 계획 수립
