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
├── common/                   # Shared modules (common-core, common-security, common-observability)
├── services/                 # auth(8089), user(8081), catalog(8084), inventory(8083), order(8085), payment(8087)
├── infra/                    # Docker, Kafka, k8s, monitoring
└── load-test/                # k6 load testing (NOT in Gradle build)
```

## Architecture

### Clean Architecture
Each service follows Clean Architecture: **api** → **application** → **domain** ← **infra**

**Constraint**: application/domain layers must NOT depend on api/infra layers.

### Data Flow
```
API (request) → Application (command) → Application (result) → API (response)
```

### Event-Driven Communication
- All inter-service communication uses **Kafka** (no synchronous REST calls between services)
- Events follow **CloudEvent** specification (see `common-core`)
- Consumers must be **idempotent**

**Kafka 작업 시 반드시 아래 문서를 참조하세요:**
- `@.claude/docs/event-publishing-pattern.md` - Publisher 구현 가이드
- `@.claude/docs/event-consuming-pattern.md` - Consumer 구현 가이드
- `@.claude/docs/service-event-matrix.md` - 서비스별 이벤트 발행/소비 매트릭스

### Key Dependencies
```kotlin
// Mono repo uses project dependencies (no GH_USER/GH_TOKEN required)
implementation(project(":common:common-core"))
implementation(project(":common:common-security"))
```

## Common Module Auto-Configuration

| Module | Auto-Configuration | Purpose |
|--------|-------------------|---------|
| common-security | `JwtSecurityAutoConfiguration` | JWT validation filters |
| common-security | `WebMvcAutoConfiguration` | `@AuthId` argument resolver |
| common-core | `CommonCoreAutoConfiguration` | GlobalExceptionHandler, ApiResponseAdvice, ObjectMapper |

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
- Flyway is removed; schema is managed via JPA `ddl-auto`

## Observability

- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboards (port 3000, admin/admin123)
- **Actuator**: Each service exposes `/actuator/prometheus`
- SSoT for monitoring: `infra/monitoring/`

## Quick Reference

```kotlin
// Exception handling
throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

// API response
return ApiResponse.success(data)

// Event publishing (직접 발행 패턴)
integrationEventPublisher.publish(
    OrderPlacedEvent(orderId = savedOrder.id!!, correlationId = savedOrder.id.toString(), causationId = UUID.randomUUID().toString())
)

// Transaction management
transactionRunner.run { orderRepository.save(order) }

// Use case annotation
@UseCase
class CreateOrderUseCase { ... }
```

## Custom Skills

| Skill | 용도 | Skill | 용도 |
|-------|------|-------|------|
| `/mono-build` | Gradle 빌드 | `/common-core-exception` | 예외 처리 |
| `/mono-kafka` | Kafka 통합 | `/common-core-event` | 이벤트 발행 |
| `/mono-clean-arch` | Clean Architecture | `/common-core-response` | API 응답 |
| `/mono-new-service` | 서비스 생성 | `/common-core-utility` | 유틸리티 |
| `/mono-querydsl` | QueryDSL | `/mono-docker` | Docker |
| `/mono-k8s` | Kubernetes | `/mono-parallel` | 병렬 작업 |

## IMPORTANT Notes

- Do not use words like `MSA` or `microservice` - this is a portfolio project for a junior developer
- Load tests (`load-test/`) must only be triggered explicitly by user
- Prometheus collects load-test metrics as well

## 상세 문서

| 문서 | 내용 |
|------|------|
| `@.claude/docs/event-publishing-pattern.md` | Integration Event 발행 패턴 |
| `@.claude/docs/event-consuming-pattern.md` | Integration Event 소비 패턴 |
| `@.claude/docs/development-commands.md` | 빌드, Docker, k8s 명령어 |
| `@.claude/docs/service-event-matrix.md` | 서비스별 이벤트 매트릭스 |

## Service Documentation

Each service has its own `.claude/CLAUDE.md` with specific guidance:
- `services/order-service/` - Order saga, state machine, 7 consumers
- `services/payment-service/` - Toss Payments, idempotency, outbox
- `services/inventory-service/` - Redis + MariaDB hybrid stock management
- `services/catalog-service/` - Products, categories, SKUs
- `services/user-service/` - User registration, profile management
- `services/auth-service/` - JWT issuance, login flow
