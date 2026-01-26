# Order Service

주문 생성부터 확정/취소까지의 전체 라이프사이클을 관리하며, Saga 패턴 기반 분산 트랜잭션 오케스트레이션을 담당하는 서비스입니다.

## 개요

- **포트**: 8085
- **데이터베이스**: MariaDB (`commerce-order` 스키마)
- **메시징**: Kafka (CloudEvent 표준)

**주요 책임**:
- 주문 생성 및 검증
- Saga 패턴 기반 분산 트랜잭션 오케스트레이션
- 상태 머신 기반 주문 상태 관리
- Outbox 패턴을 활용한 이벤트 발행 (Debezium CDC)
- 이벤트 기반 멱등성 보장

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

**의존성 방향**:
```
api ──> application ──> domain
             ↑              ↑
             └─── infra ────┘
```

## 핵심 기능

### 1. Saga 패턴 분산 트랜잭션
- Choreography 기반 (중앙 오케스트레이터 없음)
- 보상 트랜잭션으로 롤백 처리
- 상세: [docs/saga-pattern.md](docs/saga-pattern.md)

### 2. 상태 머신
- 11개 상태 정의 (INIT → CONFIRMED/CANCELLED/FAILED)
- 도메인 엔티티에 상태 전이 로직 캡슐화
- 상세: [docs/state-machine.md](docs/state-machine.md)

### 3. Outbox 패턴 + Debezium CDC
- DB 트랜잭션과 이벤트 발행 원자성 보장
- `order_outbox` 테이블 → Debezium → Kafka

### 4. 멱등성 보장
- 3중 방어: Fast-path 체크 → 상태 전이 검증 → Unique Constraint
- `order_event_idempotency` 테이블: `(event_id, action)` 유니크

## 이벤트 처리

### Published Events (발행)

| Event Type | Topic | 트리거 | 구독자 |
|------------|-------|--------|--------|
| `order.placed` | `order.placed` | 주문 생성 | Inventory, Payment |
| `order.confirmed` | `order.confirmed` | 결제 완료 후 | Inventory |
| `order.cancelled` | `order.cancelled` | 주문 취소 | Inventory |

### Consumed Events (소비)

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

### Order (주문 애그리거트)
```kotlin
class Order(
    val id: Long?,
    val userId: Long,
    var status: OrderStatus,
    val totalAmount: Money,      // 주문 원금
    val discountAmount: Money,   // 할인 금액
    val payableAmount: Money,    // 실제 결제 금액
    val items: MutableList<OrderItem>,
)
```

### OrderItem (주문 아이템)
```kotlin
class OrderItem(
    val id: Long?,
    val order: Order,
    val skuId: String,
    val quantity: Int,
    val unitPrice: Money,
    var status: OrderItemStatus,
)
```

### OrderStatus (주문 상태)
```kotlin
enum class OrderStatus {
    INIT, CREATED, RESERVED, PAYMENT_CREATED, PAYMENT_PENDING,
    PAID, CONFIRMED, PARTIALLY_REFUNDED, REFUNDED, CANCELLED, FAILED
}
```

## API 명세

상세: [docs/api-reference.md](docs/api-reference.md)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/orders` | 주문 생성 |
| GET | `/api/orders` | 주문 목록 조회 |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 |
| POST | `/api/orders/{orderId}/refund` | 환불 요청 |

## 테스트

```bash
# 전체 테스트
./gradlew :services:order-service:test

# 통합 테스트
./gradlew :services:order-service:integrationTest
```

## 설정

### 주요 토픽 매핑 (application.yml)
```yaml
order:
  topic:
    mappings:
      stock:
        reserved: stock.reserved
        reservation.failed: stock.reservation.failed
        confirmed: stock.confirmed
        confirm.failed: stock.confirm.failed
      payment:
        created: payment.created
        completed: payment.completed
        failed: payment.failed
```

## 참고 문서

- [Saga 패턴 상세](docs/saga-pattern.md)
- [상태 머신 상세](docs/state-machine.md)
- [Consumer 상세](docs/consumers.md)
- [API 명세](docs/api-reference.md)
