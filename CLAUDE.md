# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the **mono repository** version of the commerce project - a Gradle multi-module project that consolidates all services and shared modules into a single repository.

The project is a distributed system created as a portfolio for a junior backend developer. The primary goal is to learn and experiment with distributed system challenges such as transactions, data consistency, and failure handling.

### Environments
- **Local**: Docker Compose (DB, Redis, Kafka)
- **Development**: k3d (macOS)
- **Production**: k3s (Linux)

## Technology Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9.25, Java 21 |
| Framework | Spring Boot 3.5.8, Spring Cloud 2025.0.0 |
| Build | Gradle (Kotlin DSL), multi-module |
| Database | MariaDB (JPA, QueryDSL) |
| Cache | Redis |
| Messaging | Apache Kafka |
| Formatting | Spotless (ktlint 1.5.0) |
| Container | Docker, k3s/k3d |

## Project Structure

```
mono/
├── build.gradle.kts          # Root build configuration
├── settings.gradle.kts       # Module includes
├── gradle.properties         # Build properties
│
├── common/                   # Shared modules
│   ├── common-core/          # Response models, exceptions, events
│   ├── common-security/      # JWT validation, auth filters
│   └── common-observability/ # Logging, MDC
│
├── services/                 # Microservices
│   ├── auth-service/         # JWT token issuance (port 8089)
│   ├── user-service/         # User management (port 8081)
│   ├── catalog-service/      # Products & categories (port 8084)
│   ├── inventory-service/    # Stock management (port 8083)
│   ├── order-service/        # Order processing (port 8085)
│   └── payment-service/      # Payment processing (port 8087)
│
├── infra/                    # Infrastructure as Code
│   ├── docker/               # DB, Redis (docker-compose)
│   ├── kafka/                # Kafka, Debezium, Kafka UI
│   ├── k8s/                  # Kubernetes manifests
│   └── monitoring/           # Prometheus, Grafana
│
└── load-test/                # k6 load testing (NOT in Gradle build)
    ├── scripts/              # Test scenarios
    ├── config/               # Environment configs
    └── monitoring/           # k6-specific Grafana dashboards
```

## Key Differences from Separate Repositories

### Dependencies
Common modules are now **project dependencies** instead of GitHub Packages:

```kotlin
// Before (separate repos)
implementation("com.koosco:common-core:0.0.1-SNAPSHOT")

// After (mono repo)
implementation(project(":common:common-core"))
implementation(project(":common:common-security"))
```

**No GH_USER/GH_TOKEN required** for common modules.

### Flyway Removed
Database migrations are **not** managed by Flyway in this version.
- Schema is managed via JPA `ddl-auto` for development
- Migration SQL files remain in `db/migration/` for reference only

### Build Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :services:auth-service:build
./gradlew :common:common-core:build

# Run tests (all modules)
./gradlew test

# Skip tests
./gradlew build -x test

# Format code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck
```

## Architecture

### Clean Architecture
Each service follows Clean Architecture with layers:
- **api**: Controllers, DTOs (request/response)
- **application**: Use cases, commands, ports
- **domain**: Entities, value objects, domain events
- **infra**: Repositories, adapters, external integrations

**Constraint**: application/domain layers must NOT depend on api/infra layers.

### Event-Driven Communication
- All inter-service communication uses **Kafka** (no synchronous REST calls between services)
- Events follow **CloudEvent** specification (see `common-core`)
- Consumers must be **idempotent**

### Integration Event Publishing Pattern

모든 서비스는 다음 표준 패턴을 따릅니다:

**디렉토리 구조**
```
services/{service}/
├── application/
│   ├── port/
│   │   └── IntegrationEventPublisher.kt    # Port 인터페이스
│   └── contract/
│       └── {Service}IntegrationEvent.kt    # 이벤트 계약
└── infra/
    └── messaging/kafka/producer/
        └── KafkaIntegrationEventPublisher.kt  # Kafka 어댑터
```

**네이밍 컨벤션**
- Port: `IntegrationEventPublisher` (Port 접미사 사용 금지)
- Adapter: `KafkaIntegrationEventPublisher` (Adapter 접미사 사용 금지)

**발행 패턴**
```kotlin
@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventPublisher: IntegrationEventPublisher,
) {
    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        val savedOrder = orderRepository.save(order)

        // Integration Event 직접 생성 및 발행
        integrationEventPublisher.publish(
            OrderPlacedEvent(
                orderId = savedOrder.id!!,
                userId = savedOrder.userId,
                correlationId = savedOrder.id.toString(),
                causationId = UUID.randomUUID().toString(),
            ),
        )

        return CreateOrderResult(savedOrder.id!!)
    }
}
```

**핵심 원칙**
1. `@Transactional` 메서드 내에서 이벤트 발행
2. `correlationId`: 주문 ID 등 비즈니스 식별자
3. `causationId`: UUID로 생성 (이벤트 추적용)
4. CloudEvent 표준 준수 (`common-core` 활용)

### Integration Event Consuming Pattern

모든 서비스는 다음 표준 Consumer 패턴을 따릅니다:

**디렉토리 구조**
```
services/{service}/
├── application/
│   └── contract/
│       └── inbound/
│           └── {Source}Event.kt    # 수신 이벤트 DTO
├── common/
│   └── MessageContext.kt           # 상관관계 추적
└── infra/
    └── messaging/kafka/consumer/
        └── Kafka{EventName}Consumer.kt
```

**Consumer 표준 패턴**
```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(private val useCase: ReserveStockUseCase) {
    @KafkaListener(
        topics = ["\${service.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",  // property 참조 필수
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        // 1. Null/역직렬화 실패 → ack 후 skip (poison message)
        // 2. MessageContext 생성 (correlationId, causationId)
        // 3. Command 변환 및 UseCase 실행
        // 4. 비즈니스 예외 → ack (재시도 불필요)
        // 5. 인프라 예외 → throw (재시도)
    }
}
```

**핵심 규칙**
1. 클래스에 `@Validated`, 파라미터에 `@Valid` 필수
2. `groupId`는 property 참조 (`${spring.kafka.consumer.group-id}`)
3. 수동 ack 모드 (`MANUAL_IMMEDIATE`)
4. poison message는 ack 후 skip
5. 비즈니스 예외와 인프라 예외 구분 처리

### Data Flow
```
API (request) → Application (command) → Application (result) → API (response)
```

## Common Module Auto-Configuration

Common modules include Spring Auto Configuration:

| Module | Auto-Configuration | Purpose |
|--------|-------------------|---------|
| common-security | `JwtSecurityAutoConfiguration` | JWT validation filters |
| common-security | `WebMvcAutoConfiguration` | `@AuthId` argument resolver |
| common-core | `CommonCoreAutoConfiguration` | GlobalExceptionHandler, ApiResponseAdvice, ObjectMapper |

## Development Commands

### Infrastructure (from `infra/` directory)

```bash
# Start local DB and Redis
cd infra/docker && docker-compose up -d

# Start Kafka (local)
make kafka-local

# Start monitoring (Prometheus + Grafana)
cd monitoring && docker-compose up -d
```

### Kubernetes (from `infra/` directory)

```bash
make k8s-ns-create          # Create namespace
make k8s-apply-all ENV=dev  # Apply all resources
make k8s-start              # Start all services
make k8s-stop               # Stop all services
make k8s-scale REPLICAS=3   # Scale services
make k8s-restart            # Rolling restart
```

### Docker Build (from service directory)

```bash
# Build JAR first
./gradlew :services:auth-service:build

# Build Docker image
cd services/auth-service
docker build -t auth-service:latest .
```

## Load Testing

Load testing uses **k6** (JavaScript-based) and is NOT part of the Gradle build system.

### Execution Rules (CRITICAL)

- Load tests must **NOT** be executed automatically
- Load tests must only be triggered **explicitly by the user**
- Do **NOT** add load tests to CI/CD pipelines
- Be mindful of resource usage in shared environments

### Test Structure (Three-Stage Approach)

```
Smoke Test → Baseline Test → Stress Test
```

| Stage | VUs | Duration | Purpose |
|-------|-----|----------|---------|
| Smoke | 1-2 | ~30s | Verify system is functional |
| Baseline | 20-50 | 5-10min | Establish performance baseline |
| Stress | 100+ | 15-30min | Find system limits |

### Commands (from `load-test/` directory)

```bash
# Install dependencies
npm install

# Run smoke test
k6 run scripts/inventory/decrease_concurrency/smoke.test.js

# Run baseline test
k6 run scripts/inventory/decrease_concurrency/baseline.test.js

# Run stress test
k6 run scripts/inventory/decrease_concurrency/stress.test.js
```

### Metrics & Visualization

- Metrics are exported to **Prometheus**
- Results are visualized in **Grafana** (`infra/monitoring/`)
- Key metrics: Response time (P50/P95/P99), Error rate, Throughput (RPS)

See `load-test/CLAUDE.md` for detailed guidance.

## Important Constraints

1. **No cross-layer dependencies**: application/domain must not depend on api/infra
2. **No synchronous inter-service calls**: Use Kafka for all service communication
3. **Idempotent consumers**: All Kafka consumers must handle duplicate messages
4. **No backward-incompatible changes** to common modules without coordination
5. **Event publishing naming**: Port는 `IntegrationEventPublisher`, Adapter는 `KafkaIntegrationEventPublisher`로 통일
6. **No Domain Event extraction pattern**: `pullDomainEvents()` 패턴 사용 금지, Integration Event 직접 발행
7. **Consumer group ID**: property 참조 필수 (`${spring.kafka.consumer.group-id}`), hardcoding 금지

## Code Guidelines

- Prefer minimal, targeted changes over large refactors
- Follow existing package structures and naming conventions
- Run `./gradlew spotlessApply` before committing
- Do not introduce new frameworks/libraries without explicit request

## Observability

- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboards (port 3000, admin/admin123)
- **Actuator**: Each service exposes `/actuator/prometheus`

SSoT for monitoring: `infra/monitoring/`

## Quick Reference

```kotlin
// Exception handling
throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

// API response
return ApiResponse.success(data)

// Event publishing (직접 발행 패턴)
integrationEventPublisher.publish(
    OrderPlacedEvent(
        orderId = savedOrder.id!!,
        correlationId = savedOrder.id.toString(),
        causationId = UUID.randomUUID().toString(),
    ),
)

// Event consuming (Consumer 패턴)
@Component
@Validated
class KafkaEventConsumer(private val useCase: UseCase) {
    @KafkaListener(topics = ["..."], groupId = "\${spring.kafka.consumer.group-id}")
    fun onEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val context = MessageContext(
            correlationId = eventDto.correlationId,
            causationId = event.id,  // 멱등성 키로 활용
        )
        useCase.execute(command, context)
        ack.acknowledge()
    }
}

// Transaction management
transactionRunner.run { orderRepository.save(order) }

// Use case annotation
@UseCase
class CreateOrderUseCase { ... }
```

## Custom Skills

특정 기능이 필요할 때 다음 skill을 호출하세요 (`.claude/skills/` 폴더):

### mono 프로젝트 가이드

| Skill | 용도 | 사용 시점 |
|-------|------|----------|
| `/mono-build` | Gradle 빌드 명령어 | 빌드, 테스트, 포맷팅 |
| `/mono-kafka` | Kafka Producer/Consumer | 이벤트 발행/소비 구현 |
| `/mono-clean-arch` | Clean Architecture | 계층 구조, 의존성 규칙 |
| `/mono-new-service` | 새 서비스 생성 | 서비스 모듈 추가 |
| `/mono-querydsl` | QueryDSL 사용 | 복잡한 쿼리 작성 |
| `/mono-docker` | Docker 명령어 | 로컬 인프라 관리 |
| `/mono-k8s` | Kubernetes 배포 | k8s/k3d 배포 |
| `/mono-parallel` | 멀티 서비스 병렬 작업 | 여러 서비스에 동시 작업 |

### common-core 공통 모듈 사용

| Skill | 용도 | 사용 시점 |
|-------|------|----------|
| `/common-core-exception` | 예외 처리 및 에러 코드 | 도메인 에러 코드 정의, 예외 발생 |
| `/common-core-event` | Kafka 이벤트 발행 | DomainEvent 정의, CloudEvent 발행 |
| `/common-core-response` | API 응답 포맷 | ApiResponse 사용, 응답 래핑 |
| `/common-core-utility` | 유틸리티 | TransactionRunner, JsonUtils, 검증 어노테이션 |

### Skill 호출 방법

```
/mono-build        # Gradle 빌드 가이드 참조
/mono-kafka        # Kafka 통합 가이드 참조
/mono-parallel     # 멀티 서비스 병렬 작업 (subagents 사용)
/common-core-event # 이벤트 시스템 가이드 참조
```

## IMPORTANT Notes

- Do not use words like `MSA` or `microservice` - this is a portfolio project for a junior developer
- Load tests (`load-test/`) must only be triggered explicitly by user
- Prometheus collects load-test metrics as well

## Service Documentation

Each service has its own CLAUDE.md with specific guidance:
- `services/auth-service/` - JWT issuance, login flow
- `services/user-service/` - User registration, profile management
- `services/catalog-service/` - Products, categories, SKUs
- `services/inventory-service/` - Stock management (Redis + MariaDB hybrid)
- `services/order-service/` - Order saga, state machine
- `services/payment-service/` - Toss Payments integration

### Event Publishing by Service

| 서비스 | Kafka 발행 | 패턴 | 비고 |
|--------|-----------|------|------|
| order-service | O | Integration Event 직접 발행 | `@Transactional` 내 발행 |
| inventory-service | O | Integration Event 직접 발행 | 비트랜잭셔널 (Redis 특성) |
| payment-service | O | Integration Event 직접 발행 | 멱등성 저장소 사용 |
| catalog-service | O | Integration Event 직접 발행 | 표준 패턴 |
| user-service | X | - | Feign 동기 호출 (auth-service 연동) |
| auth-service | X | - | 순수 CRUD |

### Event Consuming by Service

| 서비스 | 소비 이벤트 | 멱등성 | 비고 |
|--------|------------|--------|------|
| order-service | PaymentCreated/Completed/Failed, StockReserved/ReservationFailed/Confirmed/ConfirmFailed | 상태 전이 + DB 멱등성 | 7개 Consumer |
| inventory-service | OrderPlaced/Confirmed/Cancelled, ProductSkuCreated | 상태 전이 | 4개 Consumer |
| payment-service | OrderPlaced | **DB 멱등성** | IdempotencyRepository 사용 |
| catalog-service | - | - | Consumer 없음 (Producer only) |
