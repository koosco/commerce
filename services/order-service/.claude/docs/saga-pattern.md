# Saga 패턴 분산 트랜잭션

Order Service는 Choreography 기반 Saga 패턴을 적용하여 분산 트랜잭션을 처리합니다.

## 설계 결정

- 중앙 오케스트레이터 없이 각 서비스가 이벤트를 구독/발행하는 방식
- 보상 트랜잭션(Compensating Transaction)을 통한 롤백 처리
- 상태 전이 기반 멱등성으로 재시도 안정성 확보

## 주문 생성 흐름 (Happy Path)

```
[Order Service]        [Inventory Service]      [Payment Service]
      |                        |                        |
  1. 주문 생성
     (INIT → CREATED)
      |
  2. OrderPlacedEvent 발행
      |----------------------->|
      |                   재고 예약 시도
      |                        |
      |<-----------------StockReservedEvent
  3. RESERVED 상태 전이
      |
  4. PaymentPendingEvent 전송
      |------------------------------------------------>|
      |                        |                   결제 처리
      |                        |                        |
      |<----------------------------------------PaymentCompletedEvent
  5. PAID 상태 전이
      |
  6. OrderConfirmedEvent 발행
      |----------------------->|
      |                   재고 확정 차감
      |                        |
      |<-----------------StockConfirmedEvent
  7. CONFIRMED 상태 전이
      |
   [주문 완료]
```

## 전체 Saga 흐름 (상세)

```
[User API Call]
     ↓
┌────────────────────────────────────────────────────────────────┐
│ Order Service - CreateOrderUseCase                             │
│ 1. Order 엔티티 생성 (INIT → CREATED)                          │
│ 2. OrderPlacedEvent → order_outbox 저장                        │
│ 3. DB 커밋 → Debezium이 Kafka 발행                            │
└────────────────────────────────────────────────────────────────┘
     ↓ (OrderPlacedEvent)
     ├──────────────────────────────────────────────┐
     ↓                                              ↓
┌──────────────────────────┐         ┌──────────────────────────┐
│ Inventory Service        │         │ Payment Service          │
│ 1. 재고 예약 시도        │         │ (현재 구현 없음)         │
│ 2. StockReservedEvent    │         │                          │
└──────────────────────────┘         └──────────────────────────┘
     ↓ (StockReservedEvent)
┌────────────────────────────────────────────────────────────────┐
│ Order Service - KafkaStockReservedConsumer                     │
│ 1. 멱등성 체크                                                 │
│ 2. markPaymentPending() → RESERVED → PAYMENT_PENDING          │
│ 3. 멱등성 기록                                                 │
└────────────────────────────────────────────────────────────────┘
     ↓
[사용자가 Toss Payment 승인]
     ↓
┌────────────────────────────────────────────────────────────────┐
│ Payment Service - 결제 승인 처리                               │
│ 1. PaymentCompletedEvent 발행                                  │
└────────────────────────────────────────────────────────────────┘
     ↓ (PaymentCompletedEvent)
┌────────────────────────────────────────────────────────────────┐
│ Order Service - KafkaPaymentCompletedConsumer                  │
│ 1. 멱등성 체크                                                 │
│ 2. markPaid() → PAYMENT_PENDING → PAID                         │
│ 3. OrderConfirmedEvent 발행 (재고 확정 요청)                   │
│ 4. 멱등성 기록                                                 │
└────────────────────────────────────────────────────────────────┘
     ↓ (OrderConfirmedEvent)
┌──────────────────────────┐
│ Inventory Service        │
│ 1. 예약 → 확정 전환      │
│ 2. 재고 차감             │
│ 3. StockConfirmedEvent   │
└──────────────────────────┘
     ↓ (StockConfirmedEvent)
┌────────────────────────────────────────────────────────────────┐
│ Order Service - KafkaStockConfirmedConsumer                    │
│ 1. 멱등성 체크                                                 │
│ 2. confirmStock() → PAID → CONFIRMED                           │
│ 3. 멱등성 기록                                                 │
└────────────────────────────────────────────────────────────────┘
     ↓
[주문 완료]
```

## 보상 트랜잭션 흐름

### 흐름 1: 재고 예약 실패 시

```
[Order Service]        [Inventory Service]
      |                        |
  1. StockReservationFailedEvent 수신
      |
  2. FAILED 상태 전이
      |
   [주문 실패 완료]
```

- 재고가 부족하거나 예약 실패 시 Order는 FAILED 상태로 전이
- 추가 보상 트랜잭션 불필요 (아직 결제/재고 확정 안됨)

### 흐름 2: 결제 실패 시

```
[Order Service]        [Inventory Service]
      |                        |
  1. PaymentFailedEvent 수신
      |
  2. CANCELLED 상태 전이
      |
  3. OrderCancelledEvent 발행
      |----------------------->|
      |                   예약 해제
      |                   (재고 복원)
```

- 결제 실패 시 예약된 재고를 복원해야 함
- OrderCancelledEvent를 발행하여 Inventory Service에 예약 해제 요청

### 흐름 3: 재고 확정 실패 시

```
[Order Service]        [Inventory Service]      [Payment Service]
      |                        |                        |
  1. StockConfirmFailedEvent 수신
      |
  2. CANCELLED 상태 전이
      |
  3. OrderCancelledEvent 발행
      |----------------------->|
      |                   예약 해제               (환불 플로우 - 추후 구현)
      |                   (재고 복원)
```

- 결제 완료 후 재고 확정이 실패하는 경우
- 재고 예약 해제 + 환불 처리 필요 (환불은 추후 구현)

## 보상 트랜잭션 (결제 실패) 상세

```
[결제 실패 발생]
     ↓
┌────────────────────────────────────────────────────────────────┐
│ Payment Service                                                │
│ 1. PaymentFailedEvent 발행                                     │
└────────────────────────────────────────────────────────────────┘
     ↓ (PaymentFailedEvent)
┌────────────────────────────────────────────────────────────────┐
│ Order Service - KafkaPaymentFailedConsumer                     │
│ 1. 멱등성 체크                                                 │
│ 2. cancel(PAYMENT_FAILURE) → CANCELLED                         │
│ 3. OrderCancelledEvent 발행                                    │
│ 4. 멱등성 기록                                                 │
└────────────────────────────────────────────────────────────────┘
     ↓ (OrderCancelledEvent)
┌──────────────────────────┐
│ Inventory Service        │
│ 1. 예약 해제             │
│ 2. 재고 복원             │
└──────────────────────────┘
     ↓
[주문 취소 완료 (재고 복원됨)]
```

## 핵심 설계 원칙

1. **로컬 트랜잭션**: 각 서비스는 자신의 상태만 관리
2. **이벤트 기반 조율**: 중앙 조정자 없이 이벤트 체인으로 조율
3. **보상 트랜잭션**: 실패 시 보상 이벤트 발행으로 롤백
4. **멱등성**: 모든 단계에서 중복 처리 방지

## Outbox 패턴 구현

데이터베이스 트랜잭션과 이벤트 발행 사이의 원자성을 보장합니다.

```kotlin
@Transactional
fun execute(command: CreateOrderCommand): CreateOrderResult {
    // 1. 주문 엔티티 저장
    val savedOrder = orderRepository.save(order)
    savedOrder.place()

    // 2. Outbox 테이블에 이벤트 저장 (같은 트랜잭션)
    integrationEventPublisher.publish(
        OrderPlacedEvent(
            orderId = savedOrder.id!!,
            userId = savedOrder.userId,
            correlationId = savedOrder.id.toString(),
            causationId = UUID.randomUUID().toString(),
        ),
    )
    // → OutboxIntegrationEventPublisher가 order_outbox 테이블에 INSERT

    return CreateOrderResult(savedOrder.id!!)
}
```

**Outbox Publisher 구현**:

```kotlin
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: OrderOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    @Value("\${spring.application.name}") private val source: String,
) : IntegrationEventPublisher {

    override fun publish(event: OrderIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val payload = objectMapper.writeValueAsString(cloudEvent)

        // Outbox 테이블에 저장 (도메인 로직과 동일 트랜잭션)
        val outboxEntry = OrderOutboxEntry.create(
            aggregateId = event.orderId.toString(),
            eventType = event.getEventType(),
            payload = payload,
            topic = topic,
            partitionKey = event.orderId.toString(),
        )
        outboxRepository.save(outboxEntry)
    }
}
```

**Debezium CDC**:
- `order_outbox` 테이블의 INSERT를 감지
- 자동으로 Kafka 토픽에 발행
- 데이터베이스 커밋 성공 = 이벤트 발행 보장
