# Outbox 패턴

Transactional Outbox Pattern + Debezium CDC를 통한 안정적인 이벤트 발행 패턴입니다.

## 문제 상황

결제 생성과 이벤트 발행은 원자적으로 처리되어야 합니다. 결제는 저장되었지만 이벤트 발행이 실패하면 시스템 상태가 불일치하게 됩니다.

## 해결 방법: Transactional Outbox Pattern + Debezium CDC

### Outbox 엔티티

```kotlin
@Entity
@Table(name = "payment_outbox")
class PaymentOutboxEntry(
    aggregateId: String,           // paymentId
    eventType: String,              // "payment.created"
    payload: String,                // CloudEvent JSON
    val topic: String,              // Kafka 토픽
    val partitionKey: String        // Kafka 파티션 키
) : OutboxEntry(
    aggregateId = aggregateId,
    aggregateType = "Payment",
    eventType = eventType,
    payload = payload,
    status = OutboxStatus.PENDING
)
```

### Outbox 기반 이벤트 발행

```kotlin
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: PaymentOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper
) : IntegrationEventPublisher {

    override fun publish(event: PaymentIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val payload = objectMapper.writeValueAsString(cloudEvent)

        val outboxEntry = PaymentOutboxEntry.create(
            aggregateId = event.paymentId,
            eventType = event.getEventType(),
            payload = payload,
            topic = topic,
            partitionKey = event.getPartitionKey()
        )

        // DB에 저장 (트랜잭션 내)
        outboxRepository.save(outboxEntry)
    }
}
```

## 동작 흐름

1. **비즈니스 로직 실행**: 결제 생성
2. **Outbox 저장**: 동일 트랜잭션 내에서 `payment_outbox` 테이블에 이벤트 저장
3. **CDC 감지**: Debezium이 Outbox 테이블의 INSERT를 감지
4. **Kafka 발행**: Debezium이 자동으로 Kafka에 이벤트 발행

## Outbox 테이블 구조

```sql
CREATE TABLE payment_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,     -- paymentId
    aggregate_type VARCHAR(255) NOT NULL,   -- "Payment"
    event_type VARCHAR(255) NOT NULL,       -- "payment.created"
    payload TEXT NOT NULL,                  -- CloudEvent JSON
    topic VARCHAR(255) NOT NULL,            -- Kafka 토픽
    partition_key VARCHAR(255) NOT NULL,    -- Kafka 파티션 키
    status VARCHAR(50) NOT NULL,            -- PENDING, PUBLISHED
    created_at TIMESTAMP NOT NULL,
    INDEX idx_payment_outbox_status (status, created_at)
);
```

## Debezium CDC 설정

Debezium Outbox Event Router가 `payment_outbox` 테이블을 모니터링하여 자동으로 Kafka에 발행합니다.

### 주요 설정

- **Source Table**: `payment_outbox`
- **Routing Key**: `partition_key` 컬럼 값
- **Topic**: `topic` 컬럼에 지정된 토픽
- **Payload**: `payload` 컬럼의 CloudEvent JSON

## 장점

1. **원자성 보장**: DB 저장과 이벤트 발행이 원자적으로 처리
2. **재시도 가능**: 이벤트 발행 실패 시 CDC가 자동 재시도
3. **순서 보장**: `partitionKey` 기반으로 동일 결제의 이벤트 순서 유지
4. **장애 복구**: 서비스 재시작 후에도 미발행 이벤트 자동 처리

## Outbox 장점 요약

| 특성 | 설명 |
|------|------|
| At-least-once 보장 | 이벤트가 최소 한 번은 발행됨 |
| 트랜잭션 일관성 | DB 변경과 이벤트 발행의 원자성 |
| 자동 재시도 | CDC가 실패 시 자동으로 재시도 |
| 순서 보장 | `partition_key` 기반 파티셔닝 |
