# Commerce - 이커머스 분산 시스템

> Kotlin + Spring Boot 기반의 이벤트 드리븐 분산 시스템 포트폴리오 프로젝트

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [프로젝트 구조](#-프로젝트-구조)
- [핵심 기능](#-핵심-기능)
- [시작하기](#-시작하기)
- [서비스 소개](#-서비스-소개)
- [주요 작업 내용](#-주요-작업-내용)

## 🎯 프로젝트 개요

이 프로젝트는 **분산 시스템의 핵심 과제(트랜잭션, 데이터 정합성, 장애 처리)를 학습하고 실험**하기 위해 만든 포트폴리오입니다.

완벽한 제품을 만드는 것보다, 분산 환경에서 발생하는 실제 문제들을 직접 경험하고 해결하는 과정에 집중했습니다.

### 프로젝트 목표

1. **이벤트 드리븐 아키텍처 구현**
   - Kafka를 활용한 서비스 간 비동기 통신
   - CloudEvents 표준 준수
   - Outbox 패턴 + CDC를 통한 이벤트-트랜잭션 원자성 보장

2. **분산 트랜잭션 처리**
   - Saga 패턴을 활용한 주문 프로세스 구현
   - 보상 트랜잭션(Compensating Transaction) 처리
   - 이벤트 멱등성 보장

3. **성능 및 확장성**
   - Redis를 활용한 재고 관리 성능 최적화
   - Kubernetes 환경에서의 수평 확장
   - k6 기반 부하 테스트 및 성능 측정

4. **관측성(Observability)**
   - Prometheus + Grafana를 통한 메트릭 수집 및 시각화
   - 분산 추적(correlationId, causationId)
   - 부하 테스트 결과 분석

## 🛠 기술 스택

### Backend
- **Language**: Kotlin 1.9.25, Java 21
- **Framework**: Spring Boot 3.5.8, Spring Cloud 2025.0.0
- **Build**: Gradle (Kotlin DSL), Multi-module

### Data & Cache
- **Database**: MariaDB (JPA, QueryDSL)
- **Cache**: Redis (AOF persistence)
- **Migration**: JPA DDL Auto (개발), Flyway reference

### Messaging
- **Message Broker**: Apache Kafka (KRaft mode)
- **CDC**: Debezium (Outbox pattern)
- **Event Spec**: CloudEvents 1.0

### Infrastructure
- **Container**: Docker, Docker Compose
- **Orchestration**: Kubernetes (k3s/k3d)
- **Monitoring**: Prometheus, Grafana, Node Exporter
- **Load Testing**: k6 (JavaScript)

### Code Quality
- **Formatting**: Spotless (ktlint 1.5.0)
- **Architecture**: Clean Architecture
- **Testing**: JUnit 5, Testcontainers

## 🏗 시스템 아키텍처

### 서비스 구성도

```
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│                    (Kubernetes Ingress)                     │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
   ┌────▼────┐          ┌────▼────┐          ┌────▼────┐
   │  auth   │          │  user   │          │ catalog │
   │ service │          │ service │          │ service │
   │  :8089  │          │  :8081  │          │  :8084  │
   └─────────┘          └─────────┘          └─────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
   ┌────▼────┐          ┌────▼────┐          ┌────▼────┐
   │  order  │          │inventory│          │ payment │
   │ service │          │ service │          │ service │
   │  :8085  │          │  :8083  │          │  :8087  │
   └────┬────┘          └────┬────┘          └────┬────┘
        │                    │                     │
        └────────────────────┼─────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  Kafka Cluster  │
                    │   (KRaft mode)  │
                    │      :9092      │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │    Debezium     │
                    │  (CDC Outbox)   │
                    └─────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                     │
├─────────────────────────────────────────────────────────────┤
│  MariaDB   │   Redis    │  Prometheus  │    Grafana        │
│   :3306    │   :6379    │    :9090     │     :3000         │
└─────────────────────────────────────────────────────────────┘
```

### Clean Architecture 계층 구조

```
┌──────────────────────────────────────────────┐
│              api (Presentation)              │
│  - Controllers, DTOs, Exception Handlers     │
└──────────────────┬───────────────────────────┘
                   │ depends on
┌──────────────────▼───────────────────────────┐
│          application (Use Cases)             │
│  - Commands, Results, Ports (interfaces)     │
└──────────────────┬───────────────────────────┘
                   │ depends on
┌──────────────────▼───────────────────────────┐
│             domain (Entities)                │
│  - Entities, Value Objects, Domain Events    │
└──────────────────────────────────────────────┘
                   ▲
                   │ implemented by
┌──────────────────┴───────────────────────────┐
│         infra (Adapters & Ports)             │
│  - Repositories, Kafka, Redis, External APIs │
└──────────────────────────────────────────────┘
```

## 📁 프로젝트 구조

```
mono/
├── common/                      # 공통 모듈
│   ├── common-core/             # 응답 모델, 예외, 이벤트 스키마
│   ├── common-security/         # JWT 인증, 필터
│   └── common-observability/    # 로깅, MDC
│
├── services/                    # 마이크로서비스
│   ├── auth-service/            # JWT 토큰 발행
│   ├── user-service/            # 사용자 관리
│   ├── catalog-service/         # 상품/카테고리 관리
│   ├── inventory-service/       # 재고 관리 (Redis + MariaDB)
│   ├── order-service/           # 주문 처리 (Saga 패턴)
│   └── payment-service/         # 결제 처리 (Toss Payments)
│
├── infra/                       # 인프라 코드
│   ├── docker-compose.yaml      # 로컬 인프라 (DB, Redis, Kafka)
│   ├── k8s/                     # Kubernetes 매니페스트
│   ├── config/                  # 설정 파일 (Redis, Prometheus, Grafana)
│   └── makefiles/               # Makefile 모듈
│
└── load-test/                   # k6 부하 테스트
    ├── scripts/                 # 테스트 시나리오
    └── monitoring/              # k6 Grafana 대시보드
```

## ✨ 핵심 기능

### 1. 이벤트 드리븐 아키텍처

**Outbox 패턴 + CDC를 통한 신뢰성 있는 이벤트 발행**

```kotlin
@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val outboxRepository: OrderOutboxRepository,
) {
    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        val order = orderRepository.save(order)

        // Outbox 테이블에 저장 (같은 트랜잭션)
        outboxRepository.save(OrderOutboxEntry(
            aggregateId = order.id.toString(),
            eventType = "order.placed",
            payload = objectMapper.writeValueAsString(cloudEvent),
            topic = "order.placed",
        ))

        return CreateOrderResult(order.id!!)
    }
    // Debezium CDC가 Outbox 테이블을 감시하고 Kafka로 발행
}
```

**이벤트 멱등성 보장**

```kotlin
@Component
class KafkaOrderPlacedConsumer(
    private val useCase: ReserveStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    @KafkaListener(topics = ["order.placed"])
    fun onOrderPlaced(event: CloudEvent<OrderPlacedEvent>, ack: Acknowledgment) {
        // 1. 멱등성 체크 (Fast-path)
        if (idempotencyChecker.isDuplicate(event.id, "RESERVE_STOCK")) {
            ack.acknowledge()
            return
        }

        // 2. UseCase 실행
        useCase.execute(command, context)

        // 3. 멱등성 기록 저장
        idempotencyRepository.save(
            InventoryEventIdempotency(event.id, "RESERVE_STOCK", referenceId)
        )

        ack.acknowledge()
    }
}
```

### 2. Saga 패턴 주문 프로세스

주문 생성 → 재고 예약 → 결제 → 주문 확정 흐름을 Saga 패턴으로 구현:

```
주문 생성 (ORDER_CREATED)
    ↓
재고 예약 (order.placed → inventory.reserved)
    ↓
결제 생성 (inventory.confirmed → payment.created)
    ↓
결제 완료 (payment.completed → order.confirmed)

[보상 트랜잭션]
결제 실패 → 재고 복구 → 주문 취소
```

### 3. Redis 기반 고성능 재고 관리

```kotlin
// Lua 스크립트로 원자적 재고 차감
val script = """
    local available = redis.call('HGET', KEYS[1], 'availableQuantity')
    if tonumber(available) >= tonumber(ARGV[1]) then
        redis.call('HINCRBY', KEYS[1], 'availableQuantity', -ARGV[1])
        redis.call('HINCRBY', KEYS[1], 'reservedQuantity', ARGV[1])
        return 1
    else
        return 0
    end
""".trimIndent()

redisTemplate.execute(script, keys, args)
```

### 4. 부하 테스트 및 성능 측정

```bash
# k6 스크립트로 성능 측정
k6 run --out experimental-prometheus-rw scripts/inventory/baseline.test.js

# Grafana에서 실시간 메트릭 확인
- Response Time (P50/P95/P99)
- Throughput (RPS)
- Error Rate
- Resource Usage
```

## 🚀 시작하기

### 사전 요구사항

- Java 21
- Docker & Docker Compose
- Gradle 8.x
- (선택) k3d (Kubernetes 테스트용)

### 1. 인프라 시작

```bash
cd infra

# Docker Compose로 DB, Redis, Kafka 시작
docker compose --profile full up -d

# 상태 확인
docker compose ps
```

### 2. 서비스 빌드 및 실행

```bash
# 전체 빌드
./gradlew build

# 특정 서비스 실행
./gradlew :services:order-service:bootRun

# 또는 JAR 실행
java -jar services/order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar
```

### 3. 동작 확인

```bash
# 사용자 등록
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "테스트"
  }'

# 주문 생성
curl -X POST http://localhost:8085/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"skuId": 1, "quantity": 2}],
    "shippingAddressId": 1
  }'
```

### 4. 모니터링

- **Grafana**: http://localhost:3000 (admin/admin123)
- **Prometheus**: http://localhost:9090
- **Kafka UI**: http://localhost:18080

## 📦 서비스 소개

| 서비스 | 포트 | 설명 | 상세 문서 |
|--------|------|------|-----------|
| auth-service | 8089 | JWT 토큰 발행 및 인증 | [README](services/auth-service/README.md) |
| user-service | 8081 | 사용자 관리 (등록, 프로필, 주소) | [README](services/user-service/README.md) |
| catalog-service | 8084 | 상품/카테고리 관리 | [README](services/catalog-service/README.md) |
| inventory-service | 8083 | 재고 관리 (Redis + MariaDB) | [README](services/inventory-service/README.md) |
| order-service | 8085 | 주문 처리 (Saga 패턴) | [README](services/order-service/README.md) |
| payment-service | 8087 | 결제 처리 (Toss Payments) | [README](services/payment-service/README.md) |

## 📝 주요 작업 내용

### 1. Outbox 패턴 + CDC 구현 (2025-01)

**목표**: 데이터베이스 변경과 이벤트 발행의 원자성 보장

**구현 내용**:
- 모든 서비스에 Outbox 테이블 추가
- Debezium CDC Connector 설정으로 Outbox → Kafka 자동 발행
- 기존 직접 Kafka 발행 코드를 Outbox 패턴으로 전환

**결과**:
- DB 트랜잭션과 이벤트 발행의 원자성 100% 보장
- 이벤트 유실 방지
- 서비스별 Outbox 테이블: `{service}_outbox`

### 2. 이벤트 멱등성 처리 (2025-01)

**목표**: 중복 이벤트 처리 방지

**구현 내용**:
- 서비스별 IdempotencyEntry 테이블 추가
- Consumer에서 `(eventId, action)` 유니크 제약으로 중복 검증
- Fast-path 멱등성 체크 (DB 조회 최소화)

**결과**:
- order-service: 5개 Consumer에 멱등성 적용
- inventory-service: 4개 Consumer에 멱등성 적용
- payment-service: 기존 멱등성 테이블 유지

### 3. Kafka Consumer 패턴 통일 (2025-01)

**목표**: 일관된 Consumer 구현 패턴 확립

**구현 내용**:
- `@Validated` + `@Valid` 조합으로 CloudEvent 검증
- `MessageContext`로 correlationId, causationId 전달
- poison message 처리 전략 통일
- 수동 ack 모드 (`MANUAL_IMMEDIATE`)

**결과**:
- 모든 Consumer가 동일한 패턴 준수
- 이벤트 추적 및 디버깅 용이

### 4. Integration Event 직접 발행 패턴 (2025-01)

**목표**: 도메인 이벤트 추출 패턴 제거, 단순화

**구현 내용**:
- `pullDomainEvents()` 패턴 제거
- UseCase에서 Integration Event 직접 생성 및 발행
- Port/Adapter 구조 유지 (`IntegrationEventPublisher`)

**결과**:
- 코드 복잡도 감소
- 이벤트 발행 로직 명확화
- 불필요한 추상화 제거

### 5. Gradle Multi-module 전환 (2024-12)

**목표**: 여러 저장소를 하나의 모노레포로 통합

**구현 내용**:
- common 모듈을 GitHub Packages에서 project dependency로 변경
- 서비스별 독립 빌드 및 공통 빌드 스크립트
- Spotless 코드 포맷팅 통합

**결과**:
- 빌드 시간 단축 (캐시 활용)
- 의존성 관리 단순화
- GH_USER/GH_TOKEN 불필요

### 6. Kafka Integration Test 인프라 (2024-12)

**목표**: Kafka 통합 테스트 자동화

**구현 내용**:
- Testcontainers로 Kafka 컨테이너 실행
- Producer/Consumer 통합 테스트 작성
- 이벤트 발행/소비 E2E 검증

**결과**:
- CI/CD 파이프라인에서 Kafka 테스트 가능
- 이벤트 계약 변경 감지

### 7. k6 부하 테스트 구축 (2024-11)

**목표**: 성능 기준선 수립 및 병목 지점 파악

**구현 내용**:
- Smoke → Baseline → Stress 3단계 테스트
- Prometheus Remote Write로 메트릭 수집
- Grafana 대시보드로 실시간 시각화

**결과**:
- 재고 차감 API: P95 응답시간 50ms 이하
- Redis 동시성 처리 검증
- 시스템 한계점 파악 (RPS 1000+)

## 📚 참고 문서

- [CloudEvents Specification](https://github.com/cloudevents/spec)
- [Event Contracts](common/common-core/docs/event-contracts.md)
- [Infrastructure Guide](infra/CLAUDE.md)
- [Load Testing Guide](load-test/CLAUDE.md)

## 📄 라이선스

이 프로젝트는 포트폴리오 목적으로 제작되었습니다.

---

**Contact**: [GitHub](https://github.com/koosco)
