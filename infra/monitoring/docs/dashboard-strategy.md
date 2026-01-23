# Grafana 대시보드 전략 가이드

## 🎯 대시보드 계층 구조

### Level 1: Overview Dashboard (필수 ⭐)

**목적**: 전체 시스템 상태를 한눈에 파악

**포함 내용**:

- 모든 서비스의 UP/DOWN 상태
- 각 서비스의 요청 처리량 (RPS)
- 전체 에러율
- 평균 응답 시간
- 인프라 리소스 사용률 요약

**장점**:

- 장애 발생 시 가장 먼저 확인
- 경영진/관리자에게 보고용
- 온콜 엔지니어의 첫 화면

---

### Level 2: Domain/Team Dashboard

**목적**: 팀 또는 도메인별 상세 모니터링

**카테고리별 구성**:

#### 1. Infrastructure Dashboard

- 노드(서버) 메트릭
- Docker 컨테이너 상태
- Kafka 브로커 상태

#### 2. Application Dashboard

- 모든 마이크로서비스 메트릭 (변수로 선택)
- JVM 메모리, GC
- HTTP 요청/응답
- 데이터베이스 커넥션 풀

#### 3. Business Metrics Dashboard

- 주문 수
- 결제 성공률
- 사용자 가입 수
- 재고 회전율

---

### Level 3: Service-Specific Dashboard (선택)

**목적**: 특정 서비스 Deep Dive 분석

**언제 만들까?**:

- ✅ 중요도가 높은 서비스 (Order, Payment)
- ✅ 복잡한 비즈니스 로직 (Inventory)
- ✅ 자주 장애가 발생하는 서비스
- ❌ 단순 CRUD 서비스는 Level 2로 충분

---

## 🎨 실무 권장 구성 (당신의 경우)

### 필수 대시보드 (3개)

#### 1. 🌐 System Overview

```
┌─────────────────────────────────────┐
│ System Overview (전체 현황)          │
├─────────────────────────────────────┤
│ ✅ Service Status                    │
│ Auth: UP | User: UP | Order: UP ... │
│                                      │
│ 📊 Traffic (RPS)                     │
│ [그래프] 전체 서비스 요청량           │
│                                      │
│ ⚠️ Error Rate                        │
│ [그래프] 서비스별 에러율              │
│                                      │
│ ⚡ Response Time                     │
│ [그래프] 서비스별 평균 응답 시간      │
└─────────────────────────────────────┘
```

#### 2. 🖥️ Infrastructure (Node Exporter)

```
┌─────────────────────────────────────┐
│ Infrastructure Metrics               │
├─────────────────────────────────────┤
│ 드롭다운: [Kafka Server ▼]          │
│                                      │
│ CPU Usage: 45%                       │
│ Memory: 8GB / 16GB (50%)            │
│ Disk I/O: 120 IOPS                  │
│ Network: 1.2 Gbps                   │
└─────────────────────────────────────┘
```

#### 3. 🔧 Microservices (Application)

```
┌─────────────────────────────────────┐
│ Microservice Metrics                 │
├─────────────────────────────────────┤
│ 드롭다운: [Order Service ▼]         │
│          - Auth                      │
│          - User                      │
│          - Catalog                   │
│          - Inventory                 │
│          - Payment                   │
│                                      │
│ HTTP Requests: [그래프]              │
│ JVM Heap: [그래프]                   │
│ DB Connections: [그래프]             │
│ Response Time: [그래프]              │
└─────────────────────────────────────┘
```

### 선택 대시보드 (중요 서비스)

#### 4. 💰 Payment Service (Deep Dive)

- 결제 성공/실패율
- 결제 수단별 통계
- 외부 PG사 응답 시간
- 결제 재시도 횟수

#### 5. 📦 Order Service (Deep Dive)

- 주문 생성 → 결제 → 배송 파이프라인
- 주문 상태별 분포
- 재고 차감 실패율
- Kafka 메시지 지연

---

## 🔧 Grafana 변수(Variable) 활용

### 변수를 사용하면 하나의 대시보드로 모든 서비스 커버!

#### 변수 설정 예시

**1. Service 변수 (서비스 선택)**

```yaml
Name: service
Type: Custom
Values: auth,user,catalog,inventory,order,payment
```

**2. Environment 변수**

```yaml
Name: environment
Type: Custom
Values: local,production
```

**3. Instance 변수 (서버 선택)**

```yaml
Name: instance
Type: Query
Query: label_values(up, instance)
```

#### 쿼리에서 변수 사용

```promql
# HTTP 요청 수 (서비스 변수 사용)
rate(http_server_requests_seconds_count{service="$service"}[5m])

# JVM 메모리 (서비스 + 환경 변수 사용)
jvm_memory_used_bytes{service="$service", environment="$environment"}

# CPU 사용률 (인스턴스 변수 사용)
rate(node_cpu_seconds_total{instance="$instance"}[5m])
```

#### 변수 UI 예시

```
┌─────────────────────────────────────────────────┐
│ Microservice Dashboard                          │
├─────────────────────────────────────────────────┤
│ Service: [Order Service ▼] Environment: [Prod ▼]│
│                                                 │
│ [여기서 선택한 서비스/환경에 맞는 그래프 표시]     │
└─────────────────────────────────────────────────┘
```

---

## 📋 Import할 대시보드 목록

### 즉시 사용 가능한 공식 대시보드

| ID        | 이름                            | 용도                     | 우선순위    |
| --------- | ------------------------------- | ------------------------ | ----------- |
| **1860**  | Node Exporter Full              | 인프라 모니터링          | ⭐⭐⭐ 필수 |
| **11378** | Spring Boot 2.1 System Monitor  | 애플리케이션 (변수 지원) | ⭐⭐⭐ 필수 |
| **4701**  | JVM (Micrometer)                | JVM 상세 분석            | ⭐⭐ 권장   |
| **12056** | Spring Boot Statistics          | HTTP, DB, Cache          | ⭐⭐ 권장   |
| **193**   | Docker Container & Host Metrics | 컨테이너 모니터링        | ⭐⭐ 권장   |
| **7589**  | Kafka Exporter Overview         | Kafka 모니터링           | ⭐ 선택     |
| **6417**  | Kafka Overview                  | Kafka 상세               | ⭐ 선택     |

---

## 🎨 커스텀 대시보드 생성 (추천)

### Overview Dashboard (JSON 템플릿)

**패널 구성**:

#### Row 1: Service Health

```json
{
  "title": "Service Status",
  "panels": [
    {
      "type": "stat",
      "targets": [
        {
          "expr": "up{job=~\".*-service\"}"
        }
      ],
      "options": {
        "colorMode": "background",
        "graphMode": "none"
      }
    }
  ]
}
```

#### Row 2: Traffic

```json
{
  "title": "Request Rate (RPS)",
  "panels": [
    {
      "type": "graph",
      "targets": [
        {
          "expr": "sum(rate(http_server_requests_seconds_count[5m])) by (service)"
        }
      ]
    }
  ]
}
```

#### Row 3: Errors

```json
{
  "title": "Error Rate (%)",
  "panels": [
    {
      "type": "graph",
      "targets": [
        {
          "expr": "sum(rate(http_server_requests_seconds_count{status=~\"5..\"}[5m])) by (service) / sum(rate(http_server_requests_seconds_count[5m])) by (service) * 100"
        }
      ]
    }
  ]
}
```

---

## 🚀 실전 적용 순서

### 1단계: 기본 대시보드 Import (5분)

```
1. Grafana 로그인
2. Dashboards → Import
3. ID 1860 입력 → Node Exporter Full
4. ID 11378 입력 → Spring Boot Monitor
5. ID 4701 입력 → JVM Micrometer
```

### 2단계: 변수 설정 (10분)

```
1. Spring Boot Monitor 대시보드 열기
2. Settings (⚙️) → Variables
3. New Variable 클릭
   - Name: service
   - Type: Custom
   - Values: auth,user,catalog,inventory,order,payment
4. Save Dashboard
```

### 3단계: Overview 대시보드 생성 (30분)

```
1. Create → Dashboard
2. Add Panel
3. 위 JSON 템플릿 참고하여 패널 추가
4. Save Dashboard as "Commerce System Overview"
```

---

## 💡 실무 팁

### 1. 대시보드 네이밍 규칙

```
[환경] - [계층] - [대상]

예시:
- [Prod] - Overview - Commerce System
- [Prod] - Infrastructure - All Nodes
- [Prod] - Application - Microservices
- [Prod] - Service - Payment Deep Dive
```

### 2. 폴더 구조

```
📁 General (기본)
📁 Infrastructure
   └── Node Exporter Full
   └── Docker Metrics
📁 Application
   └── Microservices Overview
   └── Spring Boot Monitor
   └── JVM Metrics
📁 Business
   └── Order Metrics
   └── Payment Analytics
📁 Alerts
   └── Critical Alerts
```

### 3. 패널 배치 순서 (위에서 아래로)

```
1. Status (현재 상태)
2. Rate (변화율)
3. Errors (에러)
4. Duration (지연시간)
5. Saturation (포화도)
```

→ **RED 메트릭** (Rate, Errors, Duration) 우선 배치!

### 4. 색상 규칙

```
- 🟢 Green: 정상 (< 70%)
- 🟡 Yellow: 주의 (70-85%)
- 🔴 Red: 위험 (> 85%)
```

### 5. 시간 범위 설정

```
- Overview: Last 15 minutes (실시간 모니터링)
- Application: Last 1 hour (트렌드 파악)
- Deep Dive: Last 6 hours ~ 1 day (분석)
```

---

## 🎯 당신의 경우 권장 구성

### 최소 구성 (3개 대시보드)

1. **System Overview** - 전체 6개 서비스 한눈에
2. **Infrastructure** - 3개 서버 (Kafka, Stress, Microservice)
3. **Microservices** - 변수로 서비스 선택

### 권장 구성 (5개 대시보드)

1. System Overview
2. Infrastructure
3. Microservices (변수)
4. **Payment Service** (중요)
5. **Order Service** (중요)

### 이상적 구성 (7개 대시보드)

1. System Overview
2. Infrastructure
3. Microservices
4. Payment Service
5. Order Service
6. **Business Metrics** (주문수, 매출, 사용자)
7. **Kafka Metrics** (메시지 큐)

---

## ⚠️ 피해야 할 실수

### ❌ 안티패턴

1. **서비스마다 개별 대시보드 6개 생성**

   - 유지보수 지옥
   - 변수 하나로 해결 가능

2. **너무 많은 패널**

   - 한 대시보드에 50개 이상 패널
   - 로딩 느림, 가독성 저하

3. **중복 대시보드**

   - 같은 메트릭을 여러 대시보드에
   - 일관성 없는 쿼리

4. **변수 미사용**
   - 반복 작업 증가
   - 확장성 저하

### ✅ 베스트 프랙티스

1. **변수 적극 활용**

   - 하나의 템플릿으로 모든 서비스

2. **계층적 구조**

   - Overview → Domain → Service

3. **명확한 네이밍**

   - 누가 봐도 이해 가능

4. **표준화된 쿼리**
   - 재사용 가능한 쿼리 패턴

---

## 📚 참고 자료

- [Grafana Variables Documentation](https://grafana.com/docs/grafana/latest/dashboards/variables/)
- [Dashboard Best Practices](https://grafana.com/docs/grafana/latest/best-practices/best-practices-for-creating-dashboards/)
- [Community Dashboards](https://grafana.com/grafana/dashboards/)
