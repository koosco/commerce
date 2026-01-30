# Consumer 상세

## 7개 Consumer 목록

Order Service는 다음 7개의 Kafka Consumer를 운영합니다.

| Consumer | 토픽 | 액션 | 상태 전이 | 멱등성 키 |
|----------|------|------|----------|-----------|
| `KafkaStockReservedConsumer` | `stock.reserved` | 재고 예약 완료 처리 | CREATED -> RESERVED | `MARK_RESERVED` |
| `KafkaStockReservationFailedConsumer` | `stock.reservation.failed` | 재고 예약 실패 보상 | CREATED -> FAILED | `MARK_FAILED_BY_STOCK_RESERVATION` |
| `KafkaPaymentCreatedConsumer` | `payment.created` | 결제 생성 처리 | RESERVED -> PAYMENT_CREATED | `MARK_PAYMENT_CREATED` |
| `KafkaPaymentCompletedConsumer` | `payment.completed` | 결제 완료 처리 | PAYMENT_PENDING -> PAID | `MARK_PAID` |
| `KafkaPaymentFailedConsumer` | `payment.failed` | 결제 실패 보상 | PAYMENT_PENDING -> CANCELLED | `CANCEL_BY_PAYMENT_FAILURE` |
| `KafkaStockConfirmedConsumer` | `stock.confirmed` | 재고 확정 처리 | PAID -> CONFIRMED | `MARK_CONFIRMED` |
| `KafkaStockConfirmFailedConsumer` | `stock.confirm.failed` | 재고 확정 실패 보상 | PAID -> CANCELLED | `CANCEL_BY_STOCK_CONFIRM_FAILURE` |

## 공통 Consumer 패턴 (6단계)

모든 Consumer는 동일한 패턴을 따릅니다.

```
1. CloudEvent 역직렬화
2. Null/poison message 처리 (ack 후 skip)
3. Fast-path 멱등성 체크
4. UseCase 실행 (상태 전이 검증)
5. 멱등성 기록
6. 수동 ack
```

### 구현 예시 (KafkaPaymentCompletedConsumer)

```kotlin
@KafkaListener(
    topics = ["\${order.topic.mappings.payment.completed}"],
    groupId = "\${spring.kafka.consumer.group-id}",
)
fun onPaymentCompleted(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
    val paymentCompleted = objectMapper.convertValue(event.data, PaymentCompletedEvent::class.java)

    // 1. Fast-path 멱등성 체크 (처리 전 조회)
    if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_PAID)) {
        logger.info("Event already processed: eventId=${event.id}")
        ack.acknowledge()
        return
    }

    try {
        // 2. 비즈니스 로직 실행 (상태 전이 검증 포함)
        markOrderPaidUseCase.execute(
            MarkOrderPaidCommand(
                orderId = paymentCompleted.orderId,
                paidAmount = paymentCompleted.paidAmount,
            ),
            MessageContext(
                correlationId = paymentCompleted.correlationId,
                causationId = event.id,
            ),
        )

        // 3. 멱등성 기록 (Unique Constraint로 race condition 방어)
        idempotencyChecker.recordProcessed(
            eventId = event.id,
            action = Actions.MARK_PAID,
            orderId = paymentCompleted.orderId,
        )

        ack.acknowledge()
    } catch (e: Exception) {
        logger.error("Failed to process PaymentCompleted event", e)
        throw e  // 재시도
    }
}
```

### 핵심 규칙

- 클래스에 `@Validated`, 파라미터에 `@Valid` 필수
- `groupId`는 property 참조 (`${spring.kafka.consumer.group-id}`)
- 수동 ack 모드 (`MANUAL_IMMEDIATE`)
- poison message는 ack 후 skip
- 비즈니스 예외와 인프라 예외 구분 처리

## IdempotencyChecker 구현

```kotlin
@Component
class IdempotencyChecker(private val idempotencyRepository: OrderIdempotencyRepository) {

    fun isAlreadyProcessed(eventId: String, action: String): Boolean =
        idempotencyRepository.existsByEventIdAndAction(eventId, action)

    fun recordProcessed(eventId: String, action: String, orderId: Long): Boolean = try {
        idempotencyRepository.save(
            OrderEventIdempotency.create(eventId, action, orderId)
        )
        true
    } catch (e: DataIntegrityViolationException) {
        // Unique constraint violation (동시 처리 감지)
        logger.info("Race condition resolved: eventId=$eventId, action=$action")
        false
    }
}
```

**동작 방식**:
- `isAlreadyProcessed()`: 처리 전 DB 조회로 대부분의 중복 사전 차단
- `recordProcessed()`: 처리 후 기록, `DataIntegrityViolationException` 발생 시 race condition으로 판단하고 안전하게 무시

## 3중 방어 전략

멱등성 보장을 위해 3개 계층의 방어선을 구축합니다.

| 방어선 | 메커니즘 | 역할 |
|--------|---------|------|
| 1차 (Fast-path) | DB 조회 (`existsByEventIdAndAction`) | 대부분의 중복을 사전 차단 |
| 2차 (상태 전이) | 도메인 엔티티 상태 머신 (`InvalidOrderStatus` 예외) | 이미 전이된 상태에서 중복 전이 차단 |
| 3차 (Unique Constraint) | `order_event_idempotency` 테이블 유니크 제약 | Race condition 최종 방어 |

### 각 방어선의 역할

**1차 - Fast-path 체크**:
- `order_event_idempotency` 테이블에서 `(event_id, action)` 조합 존재 여부 조회
- 이미 처리된 이벤트는 UseCase 실행 없이 즉시 ack 후 반환
- 대부분의 중복 이벤트를 여기서 차단

**2차 - 상태 전이 검증**:
- Order 엔티티의 상태 전이 메서드가 현재 상태를 검증
- 예: 이미 `PAID` 상태인 주문에 다시 `markPaid()` 호출 시 `InvalidOrderStatus` 예외
- Fast-path 체크가 race condition으로 통과되더라도 여기서 차단

**3차 - Unique Constraint**:
- `(event_id, action)` 유니크 제약조건으로 DB 레벨에서 최종 방어
- 동시에 두 스레드가 같은 이벤트를 처리하려 할 때, 한 쪽은 `DataIntegrityViolationException` 발생
- 예외 발생 시 이미 다른 스레드가 처리 완료한 것으로 판단하고 안전하게 무시

## 동시성 시나리오

```
Thread A                           Thread B
  |                                  |
  | isAlreadyProcessed() = false     |
  |                                  | isAlreadyProcessed() = false
  |                                  |
  | execute UseCase (성공)           |
  |                                  | execute UseCase (InvalidOrderStatus 예외)
  |                                  |   -> 상태 전이 실패 (이미 PAID)
  |                                  |
  | recordProcessed() (성공)         |
  |                                  | recordProcessed() (Unique violation)
  |                                  |   -> DataIntegrityViolationException
  | ack                              |
                                     | ack (중복 처리 스킵)
```

**시나리오 설명**:
1. Thread A와 Thread B가 동시에 같은 이벤트를 수신
2. 둘 다 Fast-path 체크를 통과 (아직 기록이 없으므로)
3. Thread A가 먼저 UseCase 실행에 성공하고 상태를 전이
4. Thread B는 이미 상태가 전이되었으므로 `InvalidOrderStatus` 예외 발생 (2차 방어)
5. Thread A가 멱등성 기록 성공
6. Thread B가 멱등성 기록 시도 시 Unique Constraint 위반 (3차 방어)
7. 결과적으로 이벤트는 정확히 한 번만 처리됨

## OrderEventIdempotency 엔티티

```kotlin
@Entity
@Table(
    name = "order_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_order_idempotency", columnNames = ["event_id", "action"]),
    ],
)
class OrderEventIdempotency(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String,  // CloudEvent ID

    @Column(name = "action", nullable = false, length = 100)
    val action: String,   // e.g., "MARK_PAID", "MARK_CONFIRMED"

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),
) {
    companion object {
        object Actions {
            const val MARK_RESERVED = "MARK_RESERVED"
            const val MARK_PAYMENT_PENDING = "MARK_PAYMENT_PENDING"
            const val MARK_PAYMENT_CREATED = "MARK_PAYMENT_CREATED"
            const val MARK_PAID = "MARK_PAID"
            const val MARK_CONFIRMED = "MARK_CONFIRMED"
            const val CANCEL_BY_PAYMENT_FAILURE = "CANCEL_BY_PAYMENT_FAILURE"
            const val MARK_FAILED_BY_STOCK_RESERVATION = "MARK_FAILED_BY_STOCK_RESERVATION"
            const val CANCEL_BY_STOCK_CONFIRM_FAILURE = "CANCEL_BY_STOCK_CONFIRM_FAILURE"
        }
    }
}
```

### 필드 설명

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | `Long` | 자동 생성 PK |
| `eventId` | `String` | CloudEvent의 고유 ID (UUID) |
| `action` | `String` | 수행된 액션 (e.g., `MARK_PAID`) |
| `orderId` | `Long` | 관련 주문 ID |
| `processedAt` | `Instant` | 처리 시각 |

### 유니크 제약조건

- `(event_id, action)` 조합이 유니크
- 같은 이벤트 ID라도 다른 액션은 별도로 처리 가능
- 이 제약조건이 race condition 발생 시 최종 방어선 역할 수행
