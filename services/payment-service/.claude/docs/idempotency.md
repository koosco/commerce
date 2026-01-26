# DB 기반 멱등성 보장

Payment Service의 중복 이벤트 처리 방지 패턴입니다.

## 문제 상황

분산 시스템에서 동일한 이벤트가 여러 번 전달될 수 있습니다 (네트워크 재시도, Kafka 재처리 등). 결제 생성 이벤트가 중복 수신되면 동일한 주문에 대해 여러 결제가 생성될 수 있습니다.

## 해결 방법: Idempotency Repository 패턴

`payment_idempotency` 테이블을 사용하여 이벤트 처리 여부를 추적합니다.

### 멱등성 엔티티 구조

```kotlin
@Entity
@Table(
    name = "payment_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_payment_idempotency",
            columnNames = ["order_id", "action", "idempotency_key"]
        )
    ]
)
class PaymentIdempotency(
    val orderId: Long,
    val action: PaymentAction,           // CREATE, APPROVE
    val idempotencyKey: String,          // CloudEvent ID (causationId)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 멱등성 보장 로직

```kotlin
@Transactional
fun execute(command: CreatePaymentCommand, context: MessageContext) {
    val idempotencyKey = requireNotNull(context.causationId) {
        "causationId(eventId) must be provided for idempotency"
    }

    try {
        // 1. 멱등성 키 저장 시도 (Unique Constraint)
        idempotencyRepository.save(
            PaymentIdempotency(
                orderId = command.orderId,
                action = PaymentAction.CREATE,
                idempotencyKey = idempotencyKey
            )
        )

        // 2. 중복 결제 체크
        if (paymentRepository.existsByOrderId(command.orderId)) {
            return
        }

        // 3. 결제 생성 및 이벤트 발행
        val savedPayment = paymentRepository.save(Payment(...))
        integrationEventPublisher.publish(PaymentCreatedEvent(...))

    } catch (e: DataIntegrityViolationException) {
        // 이미 처리된 이벤트 -> 멱등 성공 처리
        return
    }
}
```

## 핵심 포인트

1. **Unique Constraint 활용**: `(order_id, action, idempotency_key)` 복합 유니크 제약으로 중복 방지
2. **CloudEvent ID 사용**: 이벤트의 고유 ID를 멱등성 키로 활용
3. **Exception 기반 제어**: `DataIntegrityViolationException` 캐치로 중복 감지
4. **Transaction 내 처리**: 멱등성 키 저장과 비즈니스 로직을 하나의 트랜잭션으로 묶음
5. **Action별 분리**: CREATE/APPROVE 등 액션별로 독립적인 멱등성 보장

## 멱등성 키 전략

- **키 구성**: `(orderId, action, idempotencyKey)`
- **키 소스**: CloudEvent ID (Kafka 메시지의 고유 ID)
- **저장 시점**: UseCase 실행 전 (트랜잭션 시작 직후)

## 멱등성 시나리오

### 1. 동일 이벤트 재처리

```
Event 1: orderId=123, causationId=abc123
Event 2: orderId=123, causationId=abc123 (재시도)

결과: Event 2는 DataIntegrityViolationException으로 skip
```

### 2. 서로 다른 주문의 동일 이벤트 ID

```
Event 1: orderId=123, causationId=abc123
Event 2: orderId=456, causationId=abc123

결과: 둘 다 성공 (orderId가 다르므로 unique constraint 통과)
```

### 3. 동일 주문의 서로 다른 액션

```
Event 1: orderId=123, action=CREATE, causationId=abc123
Event 2: orderId=123, action=APPROVE, causationId=def456

결과: 둘 다 성공 (action이 다르므로 unique constraint 통과)
```

## Kafka Consumer 멱등성 처리

### Consumer 패턴

```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val createPaymentUseCase: CreatePaymentUseCase
) {
    @KafkaListener(
        topics = ["\${payment.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        // 1. Null 체크 및 역직렬화
        val orderPlacedEvent = objectMapper.convertValue(rawData, OrderPlacedEvent::class.java)

        // 2. MessageContext 생성 (멱등성 키 포함)
        val context = MessageContext(
            correlationId = orderPlacedEvent.correlationId,
            causationId = event.id  // 멱등성 키
        )

        try {
            createPaymentUseCase.execute(command, context)
            ack.acknowledge()
        } catch (e: DuplicatePaymentException) {
            ack.acknowledge()  // 비즈니스 중복 -> skip
        } catch (e: PaymentBusinessException) {
            ack.acknowledge()  // 비즈니스 실패 -> 재시도 불필요
        } catch (e: Exception) {
            throw e  // 인프라 예외 -> 재시도
        }
    }
}
```

### 처리 전략

1. **Poison Message 처리**: 역직렬화 실패 시 즉시 ack
2. **비즈니스 예외**: ack 후 skip (재시도해도 실패)
3. **인프라 예외**: throw하여 Kafka 재시도
4. **멱등성 보장**: `causationId`를 UseCase에 전달하여 중복 방지

## 테스트

통합 테스트에서 다음 시나리오를 검증합니다:

- 신규 이벤트 처리 시 멱등성 키 저장
- 중복 이벤트 처리 시 결제 생성 방지
- 동시 중복 이벤트 처리 (Race Condition)
- 서로 다른 주문의 동일 키 허용
- 동일 주문의 서로 다른 액션 허용
- causationId null 시 처리 실패

테스트 코드: `PaymentIdempotencyTest.kt`
