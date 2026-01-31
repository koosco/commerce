# Payment Service

결제 처리 및 Toss Payments API 연동을 담당하는 서비스입니다.

## 개요

| 항목 | 값 |
|------|-----|
| HTTP 포트 | 8087 |
| 데이터베이스 | MariaDB (`commerce-payment` 스키마) |
| Kafka Consumer Group | `payment-service-group` |

**핵심 역할**: 결제 생성 (OrderPlacedEvent 수신), 결제 승인 (Toss Payments Widget), 결제 취소 (부분/전액)

## Clean Architecture 계층 구조

```
payment-service/
├── api/                    # Controller, Request/Response DTOs
│   └── WidgetController    # Toss Payments Widget 통합
├── application/            # Use Cases, Commands, Ports
│   ├── usecase/            # CreatePaymentUseCase, ApprovePaymentUseCase
│   ├── port/               # IntegrationEventPublisher, PaymentRepository, IdempotencyRepository
│   └── contract/           # inbound/outbound 이벤트
├── domain/                 # Entities, Value Objects
│   ├── entity/             # Payment, PaymentTransaction, PaymentIdempotency, PaymentOutboxEntry
│   └── vo/Money
└── infra/                  # Adapters
    ├── messaging/kafka/    # consumer/, producer/
    ├── persist/            # Repository Adapters
    └── client/             # PaymentGateway (Port) → TossPaymentGateway (구현체)
```

## 핵심 기능

1. **결제 생성**: `OrderPlacedEvent` 수신 → DB 기반 멱등성 → 상태 `READY`
2. **결제 승인**: Toss Payments Widget Callback → `PaymentGateway` 추상화 → 성공/실패 이벤트 발행
3. **Outbox 패턴**: Debezium CDC 조합, At-least-once 이벤트 발행 보장

## 이벤트 처리

| 방향 | 이벤트 | 토픽 | 설명 |
|------|--------|------|------|
| 소비 | `OrderPlacedEvent` | `koosco.commerce.order.placed` | 결제 엔티티 생성 |
| 발행 | `PaymentCreatedEvent` | `koosco.commerce.payment.created` | 결제 생성 성공 |
| 발행 | `PaymentCompletedEvent` | `koosco.commerce.payment.completed` | 결제 승인 성공 |
| 발행 | `PaymentFailedEvent` | `koosco.commerce.payment.failed` | 결제 승인 실패 |

## 도메인 모델

상세: `@services/payment-service/.claude/docs/domain-model.md`

## 상세 문서

| 문서 | 내용 |
|------|------|
| [멱등성 보장](docs/idempotency.md) | DB 기반 멱등성 패턴, IdempotencyRepository |
| [Outbox 패턴](docs/outbox-pattern.md) | Transactional Outbox, Debezium CDC |
| [Toss Payments 연동](docs/toss-integration.md) | PaymentGateway 인터페이스, API 통합 |
| [API 레퍼런스](docs/api-reference.md) | 엔드포인트 명세 |
