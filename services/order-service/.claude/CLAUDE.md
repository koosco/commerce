# Order Service

주문 생성부터 확정/취소까지의 전체 라이프사이클을 관리하며, Saga 패턴 기반 분산 트랜잭션 오케스트레이션을 담당하는 서비스입니다.

## 개요

- **포트**: 8085
- **데이터베이스**: MariaDB (`commerce-order` 스키마)
- **메시징**: Kafka (CloudEvent 표준)

**주요 책임**: 주문 생성/검증, Saga 패턴 분산 트랜잭션, 상태 머신 기반 상태 관리, Outbox 패턴 이벤트 발행, 멱등성 보장

## Clean Architecture 계층 구조

```
order-service/
├── api/                          # REST 컨트롤러, 요청/응답 DTO
├── application/                  # UseCase, Command, Port
│   ├── usecase/                  # 비즈니스 유스케이스
│   ├── command/                  # Command 객체
│   ├── port/                     # 포트 인터페이스
│   └── contract/                 # 이벤트 계약 (inbound/outbound)
├── domain/                       # 엔티티, Value Object, 상태
│   ├── Order.kt                  # 주문 애그리거트 (상태 전이 로직)
│   ├── OrderItem.kt              # 주문 아이템
│   └── entity/                   # Outbox, Idempotency 엔티티
└── infra/                        # Repository 구현, Kafka Consumer/Producer
    ├── persist/                  # JPA Repository
    ├── outbox/                   # Outbox 패턴 구현
    ├── idempotency/              # 멱등성 체크
    └── messaging/kafka/          # 7개 Consumer + Outbox Publisher
```

## 핵심 기능

1. **Saga 패턴**: Choreography 기반, 보상 트랜잭션 롤백 — 상세: [docs/saga-pattern.md](docs/saga-pattern.md)
2. **상태 머신**: 11개 상태 (INIT → CONFIRMED/CANCELLED/FAILED) — 상세: [docs/state-machine.md](docs/state-machine.md)
3. **Outbox 패턴 + Debezium CDC**: DB 트랜잭션과 이벤트 발행 원자성 보장
4. **멱등성**: 3중 방어 (Fast-path → 상태 전이 검증 → Unique Constraint)

## 이벤트 처리

### Published Events

| Event Type | Topic | 구독자 |
|------------|-------|--------|
| `order.placed` | `order.placed` | Inventory, Payment |
| `order.confirmed` | `order.confirmed` | Inventory |
| `order.cancelled` | `order.cancelled` | Inventory |

### Consumed Events

| Event Type | 발행자 | 상태 전이 | 멱등성 키 |
|------------|--------|----------|-----------|
| `stock.reserved` | Inventory | CREATED → RESERVED | `MARK_RESERVED` |
| `stock.reservation.failed` | Inventory | CREATED → FAILED | `MARK_FAILED_BY_STOCK_RESERVATION` |
| `payment.created` | Payment | RESERVED → PAYMENT_CREATED | `MARK_PAYMENT_CREATED` |
| `payment.completed` | Payment | PAYMENT_PENDING → PAID | `MARK_PAID` |
| `payment.failed` | Payment | PAYMENT_PENDING → CANCELLED | `CANCEL_BY_PAYMENT_FAILURE` |
| `stock.confirmed` | Inventory | PAID → CONFIRMED | `MARK_CONFIRMED` |
| `stock.confirm.failed` | Inventory | PAID → CANCELLED | `CANCEL_BY_STOCK_CONFIRM_FAILURE` |

상세: [docs/consumers.md](docs/consumers.md)

## 도메인 모델

상세: `@services/order-service/.claude/docs/domain-model.md`

## API 명세

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/orders` | 주문 생성 |
| GET | `/api/orders` | 주문 목록 조회 |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 |
| POST | `/api/orders/{orderId}/refund` | 환불 요청 |

상세: [docs/api-reference.md](docs/api-reference.md)

## 참고 문서

- [Saga 패턴 상세](docs/saga-pattern.md)
- [상태 머신 상세](docs/state-machine.md)
- [Consumer 상세](docs/consumers.md)
- [API 명세](docs/api-reference.md)
