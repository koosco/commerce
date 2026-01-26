# Service Event Matrix

각 서비스별 Kafka 이벤트 발행 및 소비 현황입니다.

## Service Documentation

Each service has its own CLAUDE.md with specific guidance:
- `services/auth-service/` - JWT issuance, login flow
- `services/user-service/` - User registration, profile management
- `services/catalog-service/` - Products, categories, SKUs
- `services/inventory-service/` - Stock management (Redis + MariaDB hybrid)
- `services/order-service/` - Order saga, state machine
- `services/payment-service/` - Toss Payments integration

## Event Publishing by Service

| 서비스 | Kafka 발행 | 패턴 | 비고 |
|--------|-----------|------|------|
| order-service | O | Integration Event 직접 발행 | `@Transactional` 내 발행 |
| inventory-service | O | Integration Event 직접 발행 | 비트랜잭셔널 (Redis 특성) |
| payment-service | O | Integration Event 직접 발행 | 멱등성 저장소 사용 |
| catalog-service | O | Integration Event 직접 발행 | 표준 패턴 |
| user-service | X | - | Feign 동기 호출 (auth-service 연동) |
| auth-service | X | - | 순수 CRUD |

## Event Consuming by Service

| 서비스 | 소비 이벤트 | 멱등성 | 비고 |
|--------|------------|--------|------|
| order-service | PaymentCreated/Completed/Failed, StockReserved/ReservationFailed/Confirmed/ConfirmFailed | 상태 전이 + DB 멱등성 | 7개 Consumer |
| inventory-service | OrderPlaced/Confirmed/Cancelled, ProductSkuCreated | 상태 전이 | 4개 Consumer |
| payment-service | OrderPlaced | **DB 멱등성** | IdempotencyRepository 사용 |
| catalog-service | - | - | Consumer 없음 (Producer only) |

## Event Flow Summary

```
order-service (Producer)
    ├── OrderPlaced → inventory-service, payment-service
    ├── OrderConfirmed → inventory-service
    └── OrderCancelled → inventory-service

inventory-service (Producer)
    ├── StockReserved → order-service
    ├── StockReservationFailed → order-service
    ├── StockConfirmed → order-service
    └── StockConfirmFailed → order-service

payment-service (Producer)
    ├── PaymentCreated → order-service
    ├── PaymentCompleted → order-service
    └── PaymentFailed → order-service

catalog-service (Producer)
    └── ProductSkuCreated → inventory-service
```
