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
└── infra/                    # Infrastructure as Code
    ├── docker/               # DB, Redis (docker-compose)
    ├── kafka/                # Kafka, Debezium, Kafka UI
    ├── k8s/                  # Kubernetes manifests
    └── monitoring/           # Prometheus, Grafana
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

## Important Constraints

1. **No cross-layer dependencies**: application/domain must not depend on api/infra
2. **No synchronous inter-service calls**: Use Kafka for all service communication
3. **Idempotent consumers**: All Kafka consumers must handle duplicate messages
4. **No backward-incompatible changes** to common modules without coordination

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

// Event publishing
eventPublisher.publishDomainEvent(event, "urn:koosco:order-service")

// Transaction management
transactionRunner.run { orderRepository.save(order) }

// Use case annotation
@UseCase
class CreateOrderUseCase { ... }
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
