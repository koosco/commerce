# 🚀 Commerce Load Test Suite

k6 기반의 커머스 시스템 부하 테스트 프로젝트입니다. 재고 감소 동시성 제어, 주문 처리, 결제 시스템 등 다양한 시나리오에 대한 성능 테스트를 제공합니다.

## 📋 Table of Contents

- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Test Scenarios](#-test-scenarios)
- [Monitoring](#-monitoring)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

## ✨ Features

- **다양한 테스트 시나리오**: 재고, 주문, 결제 시스템 성능 테스트
- **3단계 부하 테스트**: Smoke, Baseline, Stress 테스트
- **실시간 모니터링**: Grafana + Prometheus + InfluxDB
- **HTML 리포트**: 상세한 테스트 결과 리포트 자동 생성
- **환경별 설정**: Local, EC2, Production 환경 지원

## 🔧 Prerequisites

### Required
- **k6**: v0.45.0 이상
- **Node.js**: v16 이상 (설정 파일용)
- **Docker**: v20.10 이상
- **Docker Compose**: v2.0 이상

### Installation

```bash
# k6 설치 (macOS)
brew install k6

# k6 설치 (Ubuntu/Debian)
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Docker 및 Docker Compose (Ubuntu)
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## 🚀 Quick Start

### 1. Clone Repository

```bash
git clone <repository-url>
cd load-test
```

### 2. Start Monitoring Stack

```bash
docker-compose up -d
```

### 3. Run Test

```bash
# Smoke Test (빠른 검증)
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/smoke.test.js

# Baseline Test (성능 기준선)
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/baseline.test.js

# Stress Test (한계 테스트)
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/stress.test.js
```

### 4. View Results

- **Grafana Dashboard**: http://localhost:3000
  - Username: `admin`
  - Password: `admin123`
  - Navigate to: Dashboards → Load Testing → k6 Load Test

- **HTML Reports**: `results/inventory/decrease_concurrency/`
  - `smoke.test.result.html`
  - `baseline.test.result.html`
  - `stress.test.result.html`

## 📊 Test Scenarios

### Inventory Decrease Concurrency

재고 감소 동시성 제어 테스트 시나리오입니다. 다수의 사용자가 동시에 재고를 감소시킬 때 데이터 무결성을 검증합니다.

#### Test Types

| Test Type | VUs | Duration | Purpose | Pass Criteria |
|-----------|-----|----------|---------|---------------|
| **Smoke** | 2 | 30s | 기본 기능 검증 | P95 < 1000ms, Error < 10% |
| **Baseline** | 20-50 | 7min | 성능 기준선 설정 | P95 < 800ms, Error < 5% |
| **Stress** | 100-500 | 20min | 시스템 한계 파악 | P95 < 1000ms, P99 < 2000ms, Error < 1% |

#### API Endpoint

```
POST {inventoryService}/api/v1/inventory/decrease
Content-Type: application/json

{
  "skuId": "00008217-b1ae-4045-9500-2d4b9fffaa32",
  "quantity": 2
}
```

#### Expected Responses

- **200 OK**: 재고 감소 성공
- **500 Internal Server Error**: 서버 오류

#### Files

- 📄 [Test Documentation](scripts/inventory/decrease_concurrency/README.md)
- 🧪 [Smoke Test](scripts/inventory/decrease_concurrency/smoke.test.js)
- 📈 [Baseline Test](scripts/inventory/decrease_concurrency/baseline.test.js)
- ⚡ [Stress Test](scripts/inventory/decrease_concurrency/stress.test.js)

## 📊 Monitoring

### Architecture

```
┌─────────────┐
│   k6 Test   │
└─────┬───────┘
      │ (remote write)
      ▼
┌──────────────┐
│  Prometheus  │
│    (9090)    │
└─────┬────────┘
      ▼
┌─────────────┐
│   Grafana   │
│   (3000)    │
└─────────────┘
```

### Services

| Service | Port | Purpose | Credentials |
|---------|------|---------|-------------|
| **Grafana** | 3000 | 시각화 대시보드 | admin / admin123 |
| **Prometheus** | 9090 | k6 메트릭 저장 & 시스템 메트릭 수집 | - |

### Quick Commands

```bash
# 모니터링 스택 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 모니터링 스택 중지
docker-compose down

# 데이터 포함 완전 삭제
docker-compose down -v
```

### Dashboard Panels

- **Virtual Users**: 현재 실행 중인 가상 사용자 수
- **Avg Response Time**: 평균 응답 시간 (ms)
- **Error Rate**: 요청 실패율 (%)
- **HTTP Request Duration**: Avg, P95, P99 응답 시간 추이
- **Requests Per Second**: 초당 요청 처리량

📚 **Detailed Monitoring Guide**: [monitoring/README.md](monitoring/README.md)

## ⚙️ Configuration

### Environment Files

```
config/
├── local.js          # 로컬 개발 환경
├── ec2.js           # EC2 환경
└── production.js    # 운영 환경
```

### Configuration Structure

```javascript
// config/local.js
export const config = {
  authService: "http://localhost:8089",
  userService: "http://localhost:8081",
  catalogService: "http://localhost:8084",
  inventoryService: "http://localhost:8083",
  orderService: "http://localhost:8085",
  paymentService: "http://localhost:8087",
};
```

### Using Configuration

```javascript
import { config } from "../../../config/local.js";

const BASE_URL = config.inventoryService;
```

## 📁 Project Structure

```
load-test/
├── config/                          # 환경별 설정
│   ├── local.js
│   ├── ec2.js
│   └── production.js
│
├── scripts/                         # 테스트 스크립트
│   └── inventory/
│       └── decrease_concurrency/
│           ├── README.md            # 테스트 문서
│           ├── smoke.test.js        # Smoke 테스트
│           ├── baseline.test.js     # Baseline 테스트
│           └── stress.test.js       # Stress 테스트
│
├── results/                         # HTML 리포트
│   └── inventory/
│       └── decrease_concurrency/
│           ├── smoke.test.result.html
│           ├── baseline.test.result.html
│           └── stress.test.result.html
│
├── data/                            # 테스트 데이터
│   └── inventory/
│       └── skus.json
│
├── monitoring/                      # 모니터링 설정
│   ├── README.md                    # 모니터링 가이드
│   ├── prometheus/
│   │   └── prometheus.yml
│   └── grafana/
│       ├── provisioning/
│       │   ├── datasources/
│       │   │   └── datasources.yml
│       │   └── dashboards/
│       │       └── dashboards.yml
│       └── dashboards/
│           └── k6-load-test.json
│
├── docker-compose.yml               # 모니터링 스택
└── README.md                        # 이 문서
```

## 🔍 Result Analysis

### HTML Reports

각 테스트 실행 후 `results/` 디렉토리에 HTML 리포트가 자동 생성됩니다.

```bash
# 리포트 열기
open results/inventory/decrease_concurrency/smoke.test.result.html
open results/inventory/decrease_concurrency/baseline.test.result.html
open results/inventory/decrease_concurrency/stress.test.result.html
```

### Grafana Dashboard

실시간 모니터링 및 과거 테스트 결과 분석:

1. http://localhost:3000 접속
2. admin / admin123 로그인
3. Dashboards → Load Testing → k6 Load Test
4. 시간 범위 선택하여 특정 테스트 결과 확인

### Prometheus Query

```bash
# Prometheus UI 접속
open http://localhost:9090

# 또는 CLI로 쿼리 (curl 사용)
curl 'http://localhost:9090/api/v1/query?query=k6_vus'
curl 'http://localhost:9090/api/v1/query?query=rate(k6_http_reqs_total[1m])'

# PromQL 예제
# - 평균 응답 시간: rate(k6_http_req_duration_sum[1m]) / rate(k6_http_req_duration_count[1m])
# - P95: histogram_quantile(0.95, rate(k6_http_req_duration_bucket[1m]))
# - 에러율: rate(k6_http_req_failed_total[1m]) / rate(k6_http_reqs_total[1m])
```

## 🛠️ Development

### Adding New Test

1. 테스트 스크립트 작성
```bash
scripts/{service}/{scenario}/{test-type}.test.js
```

2. 결과 저장 경로 설정
```javascript
export function handleSummary(data) {
  return {
    "results/{service}/{scenario}/{test-type}.test.result.html": html,
    stdout: JSON.stringify(data, null, 2),
  };
}
```

3. README 작성
```bash
scripts/{service}/{scenario}/README.md
```

### Running Tests in CI/CD

```yaml
# .github/workflows/load-test.yml
name: Load Test

on:
  schedule:
    - cron: '0 0 * * 0'  # Weekly

jobs:
  smoke-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run k6 Smoke Test
        uses: grafana/k6-action@v0.3.0
        with:
          filename: scripts/inventory/decrease_concurrency/smoke.test.js
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-test`)
3. Commit your changes (`git commit -m 'Add amazing test'`)
4. Push to the branch (`git push origin feature/amazing-test`)
5. Open a Pull Request

## 📚 References

### k6 Documentation
- [k6 Documentation](https://k6.io/docs/)
- [k6 HTTP Module](https://k6.io/docs/javascript-api/k6-http/)
- [k6 Metrics](https://k6.io/docs/using-k6/metrics/)
- [k6 Thresholds](https://k6.io/docs/using-k6/thresholds/)

### Monitoring
- [k6 + InfluxDB + Grafana](https://k6.io/docs/results-visualization/influxdb-+-grafana/)
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [Prometheus Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)

### Load Testing Best Practices
- [Load Testing Best Practices](https://k6.io/docs/testing-guides/load-testing-websites/)
- [Performance Testing Types](https://k6.io/docs/test-types/introduction/)

## 📝 License

MIT License

## 👥 Authors

- Your Team Name
- Contact: your-email@example.com

---

**Last Updated**: 2024
**Version**: 1.0.0
