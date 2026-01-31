# Inventory Service

재고 관리 서비스 - Redis와 MariaDB를 활용한 하이브리드 재고 관리 시스템

## 개요

| 항목 | 설명 |
|------|------|
| 포트 | 8083 |
| 데이터베이스 | MariaDB (`commerce-inventory`) |
| 캐시 | Redis (Primary Store) |
| 메시징 | Apache Kafka |

### 주요 기능

- **재고 초기화**: SKU별 초기 재고 설정
- **재고 예약**: 주문 생성 시 재고 예약 (OrderPlaced 이벤트 소비)
- **재고 확정**: 결제 성공 시 예약 재고 확정 (OrderConfirmed 이벤트 소비)
- **재고 취소**: 주문 취소 시 예약 재고 복구 (OrderCancelled 이벤트 소비)
- **재고 조회**: 실시간 재고 현황 조회 (Redis + MariaDB 스냅샷)

## Clean Architecture 계층 구조

```
inventory-service/
├── api/                          # API Layer
│   ├── controller/               # REST Controllers
│   ├── request/                  # Request DTOs
│   └── response/                 # Response DTOs
│
├── application/                  # Application Layer
│   ├── usecase/                  # Use Cases (ReserveStockUseCase, ConfirmStockUseCase 등)
│   ├── command/                  # Command DTOs
│   ├── port/                     # Ports (IntegrationEventPublisher, InventoryStockStorePort 등)
│   └── contract/                 # Event Contracts (inbound/outbound)
│
├── domain/                       # Domain Layer
│   ├── entity/                   # Inventory, InventorySnapshot, InventoryOutboxEntry
│   ├── vo/                       # Stock Value Object
│   └── exception/                # Domain Exceptions
│
└── infra/                        # Infrastructure Layer
    ├── storage/primary/          # RedisInventoryStockAdapter
    ├── messaging/kafka/          # Consumers & Producers
    ├── idempotency/              # IdempotencyChecker
    └── outbox/                   # Outbox Pattern Support
```

**계층 의존성**: `api -> application -> domain <- infra`

## 핵심 기능 요약

### Lua Script 기반 원자적 재고 연산

| 연산 | Lua Script | 설명 |
|------|-----------|------|
| **Reserve** | `reserve_stock.lua` | 가용 재고 확인 후 예약 재고로 전환 |
| **Confirm** | `confirm_stock.lua` | 예약 재고 확정 (예약 재고 감소) |
| **Cancel** | `cancel_stock.lua` | 예약 재고 취소 후 가용 재고로 복구 |
| **Decrease** | `decrease_stock.lua` | 전체 재고에서 직접 차감 |

### 재고 상태 모델

```kotlin
@Embeddable
data class Stock(
    val total: Int,      // 전체 재고
    val reserved: Int,   // 예약 재고
) {
    val available: Int   // 가용 재고 = total - reserved
        get() = total - reserved
}
```

## 이벤트 처리

### 소비 이벤트 (Inbound)

| 이벤트 | 토픽 | 처리 내용 |
|--------|------|----------|
| `OrderPlaced` | `koosco.commerce.order.placed` | 재고 예약 |
| `OrderConfirmed` | `koosco.commerce.order.confirmed` | 재고 확정 |
| `OrderCancelled` | `koosco.commerce.order.cancelled` | 재고 취소 |
| `ProductSkuCreated` | `koosco.commerce.sku.created` | 재고 초기화 |

### 발행 이벤트 (Outbound)

| 이벤트 | 토픽 | 발행 시점 |
|--------|------|----------|
| `StockReserved` | `koosco.commerce.stock.reserved` | 재고 예약 성공 |
| `StockReservationFailed` | `koosco.commerce.stock.reservation.failed` | 재고 예약 실패 |
| `StockConfirmed` | `koosco.commerce.stock.confirmed` | 재고 확정 성공 |
| `StockConfirmFailed` | `koosco.commerce.stock.confirm.failed` | 재고 확정 실패 |

## 도메인 모델

### Inventory Entity

- `skuId`: SKU 식별자
- `stock`: Stock VO (total, reserved)
- 스냅샷 저장용 Entity

### InventoryOutboxEntry

- Outbox Pattern 지원
- Debezium CDC로 Kafka 발행

### InventoryEventIdempotency

- 멱등성 보장용 테이블
- `eventId` + `action` 유니크 제약조건

## Quick Reference

```kotlin
// 재고 예약 UseCase
reserveStockUseCase.execute(command, context)

// Redis Lua Script 실행
val result = exec(reserveStockScript, stockKey, reservedKey, quantity)

// 멱등성 체크
if (idempotencyChecker.isAlreadyProcessed(eventId, action)) {
    ack.acknowledge()
    return
}
```

## Redis 연결

- **자동 설정**: `spring-boot-starter-data-redis`가 LettuceConnectionFactory를 자동 구성
- **RedisConfig**: `RedisTemplate<String, String>` 빈만 정의 (connectionFactory 빈 직접 생성 금지)
- **환경 변수**: `REDIS_HOST`, `REDIS_PORT` (ConfigMap에서 주입)

## 상세 문서

- [Redis + MariaDB 하이브리드 재고 관리](docs/redis-mariadb-hybrid.md)
