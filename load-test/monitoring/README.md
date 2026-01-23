# 📊 k6 Load Test Monitoring Stack

Docker Compose 기반 모니터링 환경 구성 가이드입니다. Prometheus와 Grafana를 사용하여 k6 테스트 결과를 실시간으로 모니터링할 수 있습니다.

## 🏗️ Stack Architecture

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

- **Prometheus**: k6 메트릭 저장 및 시스템 메트릭 수집 (Remote Write Receiver 활성화)
- **Grafana**: 통합 시각화 대시보드

## 🚀 Quick Start

### 1. 모니터링 스택 시작

```bash
# 프로젝트 루트에서 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 상태 확인
docker-compose ps
```

### 2. k6 테스트 실행 (Prometheus Remote Write)

```bash
# Smoke Test
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/smoke.test.js

# Baseline Test
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/baseline.test.js

# Stress Test
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/stress.test.js
```

### 3. 대시보드 접속

| Service    | URL                   | Credentials      |
| ---------- | --------------------- | ---------------- |
| Grafana    | http://localhost:3000 | admin / admin123 |
| Prometheus | http://localhost:9090 | -                |

**Grafana 대시보드**:

- 좌측 메뉴 → Dashboards → Load Testing → k6 Load Test - Inventory Decrease Concurrency

## 📦 Services Configuration

### Prometheus

- **Port**: 9090
- **Config**: `monitoring/prometheus/prometheus.yml`
- **Volume**: `prometheus-data` (persistent)
- **Remote Write**: 활성화 (k6 메트릭 수신)
- **Features**: Exemplar storage, Native histograms
- **Targets**: prometheus, grafana

### Grafana

- **Port**: 3000
- **Admin**: admin / admin123
- **Volume**: `grafana-data` (persistent)
- **Auto-provisioning**: datasources, dashboards
- **Datasource**: Prometheus (default)

## 🖥️ Environment-Specific Setup

### 로컬 환경 (Local)

```bash
# 기본 설정으로 실행
docker-compose up -d

# k6 테스트
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/smoke.test.js
```

### EC2 환경

#### 1. Security Group 설정

```
Inbound Rules:
- 3000 (Grafana)  - Source: Your IP or VPC
- 9090 (Prometheus) - Source: VPC only (k6 remote write)
```

#### 2. Docker & Docker Compose 설치

```bash
# Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 3. 모니터링 스택 시작

```bash
# 프로젝트 클론 또는 파일 복사 후
cd /path/to/load-test
docker-compose up -d
```

#### 4. k6 테스트 실행

```bash
# EC2 내부에서 실행
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/smoke.test.js

# 외부에서 EC2로 메트릭 전송 (Prometheus 접근 가능한 경우)
K6_PROMETHEUS_RW_SERVER_URL=http://<EC2-PUBLIC-IP>:9090/api/v1/write \
k6 run --out experimental-prometheus-rw \
  scripts/inventory/decrease_concurrency/smoke.test.js
```

#### 5. 대시보드 접속

```
http://<EC2-PUBLIC-IP>:3000
Username: admin
Password: admin123
```

## 📊 Dashboard Panels

### 1. Virtual Users (VUs)

- 현재 실행 중인 가상 사용자 수
- 실시간 부하 모니터링
- **Query**: `k6_vus`

### 2. Average Response Time

- 평균 응답 시간 (ms)
- Threshold: 500ms (Yellow), 1000ms (Red)
- **Query**: `rate(k6_http_req_duration_sum[1m]) / rate(k6_http_req_duration_count[1m])`

### 3. Error Rate

- 요청 실패율 (%)
- Threshold: 0.01 (1%) 이상 시 Red
- **Query**: `rate(k6_http_req_failed_total[1m]) / rate(k6_http_reqs_total[1m])`

### 4. HTTP Request Duration

- Avg, P95, P99 응답 시간 추이
- 1분 단위 집계
- 시계열 그래프
- **Queries**:
  - Avg: `rate(k6_http_req_duration_sum[1m]) / rate(k6_http_req_duration_count[1m])`
  - P95: `histogram_quantile(0.95, rate(k6_http_req_duration_bucket[1m]))`
  - P99: `histogram_quantile(0.99, rate(k6_http_req_duration_bucket[1m]))`

### 5. Requests Per Second (RPS)

- 초당 요청 처리량
- 시스템 처리 능력 확인
- **Query**: `rate(k6_http_reqs_total[1m])`

## 🔧 Advanced Configuration

### k6 Prometheus Remote Write 옵션

```bash
# 기본 사용
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw script.js

# 추가 옵션
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM=true \
K6_PROMETHEUS_RW_PUSH_INTERVAL=1s \
k6 run --out experimental-prometheus-rw script.js
```

### 환경변수 옵션

| Variable                                     | Default | Description                             |
| -------------------------------------------- | ------- | --------------------------------------- |
| `K6_PROMETHEUS_RW_SERVER_URL`                | -       | Prometheus remote write endpoint (필수) |
| `K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM` | false   | Native histogram 사용                   |
| `K6_PROMETHEUS_RW_PUSH_INTERVAL`             | 1s      | 메트릭 전송 주기                        |
| `K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY`  | false   | TLS 검증 스킵                           |

### Prometheus 쿼리 예제

```promql
# 평균 응답 시간
rate(k6_http_req_duration_sum[5m]) / rate(k6_http_req_duration_count[5m])

# P95 응답 시간
histogram_quantile(0.95, rate(k6_http_req_duration_bucket[5m]))

# 에러율
rate(k6_http_req_failed_total[5m]) / rate(k6_http_reqs_total[5m])

# 초당 요청 수
rate(k6_http_reqs_total[1m])

# 현재 VUs
k6_vus

# 특정 HTTP 상태 코드 필터
rate(k6_http_reqs_total{status="200"}[1m])
```

### k6 experimental-prometheus-rw 지원 확인

```bash
# k6 버전 확인 (v0.40.0 이상 필요)
k6 version

# k6 업데이트
brew upgrade k6  # macOS
# 또는 최신 버전 재설치
```

## 📁 File Structure

```
monitoring/
├── prometheus/
│   └── prometheus.yml           # Prometheus 설정 (remote write receiver 활성화)
├── grafana/
│   ├── provisioning/
│   │   ├── datasources/
│   │   │   └── datasources.yml  # Prometheus 자동 설정
│   │   └── dashboards/
│   │       └── dashboards.yml   # 대시보드 자동 프로비저닝
│   └── dashboards/
│       └── k6-load-test.json    # k6 대시보드 (Prometheus 쿼리)
└── README.md                     # 이 문서
```

## 🔗 Related Files

- [docker-compose.yml](../docker-compose.yml) - 모니터링 스택 정의
- [Prometheus Config](prometheus/prometheus.yml) - 메트릭 수집 및 remote write 설정
- [Grafana Datasources](grafana/provisioning/datasources/datasources.yml) - 데이터소스 자동 설정
- [k6 Dashboard](grafana/dashboards/k6-load-test.json) - 대시보드 JSON (Prometheus 쿼리)

## 📚 References

- [k6 Prometheus Remote Write](https://k6.io/docs/results-output/real-time/prometheus-remote-write/)
- [Prometheus Remote Write](https://prometheus.io/docs/prometheus/latest/storage/#remote-storage-integrations)
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [Prometheus Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Docker Compose](https://docs.docker.com/compose/)

## 💡 Why Prometheus (not InfluxDB)?

### Prometheus가 표준인 이유

1. **k6 공식 지원**: `experimental-prometheus-rw` 네이티브 지원
2. **Observability 표준**: 사실상 현대 observability 표준
3. **통합 가능성**: 인프라/애플리케이션/부하테스트 메트릭 통합 가능
4. **단순한 아키텍처**: k6 → Prometheus → Grafana (중간 계층 불필요)
5. **강력한 쿼리**: PromQL을 통한 유연한 메트릭 분석

### 이전 InfluxDB 구조의 문제점

```
❌ 구 아키텍처 (문제):
k6 → InfluxDB → Prometheus → Grafana
     (push)    (pull - 비정상)

- Prometheus는 Pull 모델, InfluxDB는 Push 모델
- Prometheus가 InfluxDB를 긁는 구조는 비효율적
- 데이터 중복 (같은 메트릭을 두 DB에 저장)
- 관리 포인트 증가
- 장애 지점 증가 (InfluxDB 죽으면 전체 경로 영향)
```

```
✅ 신 아키텍처 (현재):
k6 → Prometheus → Grafana
   (remote write)

- k6가 Prometheus에 직접 메트릭 전송 (remote write)
- 단일 데이터 저장소 (Prometheus)
- 단순하고 안정적인 구조
- Prometheus 표준 준수
```
