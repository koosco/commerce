# Payment Service

결제 처리 및 Toss Payments API 연동을 담당하는 서비스입니다.

## 개요

### 서비스 역할

- **결제 생성**: `OrderPlacedEvent` 수신 시 자동으로 결제 엔티티 생성
- **결제 승인**: Toss Payments Widget을 통한 결제 승인 처리
- **결제 취소**: 승인된 결제의 부분/전액 취소 처리
- **Toss Payments 연동**: 실제 결제 게이트웨이와 통합

### 포트 정보

| 항목 | 값 |
|------|-----|
| HTTP 포트 | 8087 |
| 데이터베이스 | MariaDB (`commerce-payment` 스키마) |
| Kafka Consumer Group | `payment-service-group` |

## Clean Architecture 계층 구조

```
payment-service/
├── api/                    # Controller, Request/Response DTOs
│   └── WidgetController    # Toss Payments Widget 통합
│
├── application/            # Use Cases, Commands, Ports
│   ├── usecase/
│   │   ├── CreatePaymentUseCase       # 결제 생성
│   │   └── ApprovePaymentUseCase      # 결제 승인
│   ├── port/
│   │   ├── IntegrationEventPublisher  # 이벤트 발행 Port
│   │   ├── PaymentRepository          # 결제 저장소 Port
│   │   └── IdempotencyRepository      # 멱등성 저장소 Port
│   └── contract/
│       ├── inbound/order/             # 수신 이벤트
│       └── outbound/payment/          # 발행 이벤트
│
├── domain/                 # Entities, Value Objects
│   ├── entity/
│   │   ├── Payment                    # Aggregate Root
│   │   ├── PaymentTransaction         # 결제 트랜잭션
│   │   ├── PaymentIdempotency         # 멱등성 엔티티
│   │   └── PaymentOutboxEntry         # Outbox 엔티티
│   └── vo/Money                       # 금액 Value Object
│
└── infra/                  # Adapters
    ├── messaging/kafka/
    │   ├── consumer/                  # Kafka Consumer
    │   └── producer/                  # Outbox 기반 발행
    ├── persist/                       # Repository Adapters
    └── client/
        ├── PaymentGateway             # Port 인터페이스
        └── TossPaymentGateway         # Toss API 구현체
```

### 의존성 규칙

- **application/domain -> api/infra 의존 금지**
- Domain은 순수 비즈니스 로직만 포함
- Port/Adapter 패턴으로 외부 의존성 분리

## 핵심 기능

### 1. 결제 생성

- `OrderPlacedEvent` 수신 시 자동 생성
- DB 기반 멱등성 보장 (IdempotencyRepository 패턴)
- 결제 상태 `READY`로 초기화

### 2. 결제 승인

- Toss Payments Widget Callback 처리
- `PaymentGateway` 인터페이스로 PG사 추상화
- 성공: `PaymentCompletedEvent` 발행
- 실패: `PaymentFailedEvent` 발행

### 3. Outbox 패턴

- 트랜잭션 일관성을 위한 Outbox + Debezium CDC 조합
- At-least-once 이벤트 발행 보장

## 이벤트 처리

### 소비 이벤트

| 이벤트 | 토픽 | 설명 |
|--------|------|------|
| `OrderPlacedEvent` | `koosco.commerce.order.placed` | 결제 엔티티 생성 |

### 발행 이벤트

| 이벤트 | 토픽 | 트리거 |
|--------|------|--------|
| `PaymentCreatedEvent` | `koosco.commerce.payment.created` | 결제 생성 성공 |
| `PaymentCompletedEvent` | `koosco.commerce.payment.completed` | 결제 승인 성공 |
| `PaymentFailedEvent` | `koosco.commerce.payment.failed` | 결제 승인 실패 |

## 도메인 모델

### Payment (Aggregate Root)

```kotlin
class Payment(
    val paymentId: UUID,
    val orderId: Long,
    val userId: Long,
    val amount: Money
) {
    var status: PaymentStatus = PaymentStatus.READY

    fun approve(transaction: PaymentTransaction)
    fun fail(transaction: PaymentTransaction)
    fun cancel(transaction: PaymentTransaction)
}
```

### PaymentStatus

- `READY`: 결제 준비 완료
- `APPROVED`: 승인 완료
- `FAILED`: 승인 실패
- `CANCELED`: 결제 취소

## 상세 문서

| 문서 | 내용 |
|------|------|
| [멱등성 보장](docs/idempotency.md) | DB 기반 멱등성 패턴, IdempotencyRepository |
| [Outbox 패턴](docs/outbox-pattern.md) | Transactional Outbox, Debezium CDC |
| [Toss Payments 연동](docs/toss-integration.md) | PaymentGateway 인터페이스, API 통합 |
| [API 레퍼런스](docs/api-reference.md) | 엔드포인트 명세 |

## 빠른 참조

### 빌드 및 실행

```bash
# 빌드
./gradlew :services:payment-service:build

# 테스트
./gradlew :services:payment-service:test

# 실행
./gradlew :services:payment-service:bootRun
```

### 주요 테스트

```bash
# 멱등성 테스트
./gradlew :services:payment-service:test --tests PaymentIdempotencyTest

# Kafka 통합 테스트
./gradlew :services:payment-service:test --tests KafkaOrderPlacedConsumerIntegrationTest
```
