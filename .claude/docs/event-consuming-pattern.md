# Integration Event Consuming Pattern

모든 서비스는 다음 표준 Consumer 패턴을 따릅니다.

## 디렉토리 구조

```
services/{service}/
├── application/
│   └── contract/
│       └── inbound/
│           └── {Source}Event.kt    # 수신 이벤트 DTO
├── common/
│   └── MessageContext.kt           # 상관관계 추적
└── infra/
    └── messaging/kafka/consumer/
        └── Kafka{EventName}Consumer.kt
```

## Consumer 표준 패턴

```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(private val useCase: ReserveStockUseCase) {
    @KafkaListener(
        topics = ["\${service.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",  // property 참조 필수
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        // 1. Null/역직렬화 실패 → ack 후 skip (poison message)
        // 2. MessageContext 생성 (correlationId, causationId)
        // 3. Command 변환 및 UseCase 실행
        // 4. 비즈니스 예외 → ack (재시도 불필요)
        // 5. 인프라 예외 → throw (재시도)
    }
}
```

## 핵심 규칙

1. 클래스에 `@Validated`, 파라미터에 `@Valid` 필수
2. `groupId`는 property 참조 (`${spring.kafka.consumer.group-id}`)
3. 수동 ack 모드 (`MANUAL_IMMEDIATE`)
4. poison message는 ack 후 skip
5. 비즈니스 예외와 인프라 예외 구분 처리

## Quick Reference

```kotlin
// Event consuming (Consumer 패턴)
@Component
@Validated
class KafkaEventConsumer(private val useCase: UseCase) {
    @KafkaListener(topics = ["..."], groupId = "\${spring.kafka.consumer.group-id}")
    fun onEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val context = MessageContext(
            correlationId = eventDto.correlationId,
            causationId = event.id,  // 멱등성 키로 활용
        )
        useCase.execute(command, context)
        ack.acknowledge()
    }
}
```

## 관련 제약사항

- **Consumer group ID**: property 참조 필수 (`${spring.kafka.consumer.group-id}`), hardcoding 금지
- **Idempotent consumers**: All Kafka consumers must handle duplicate messages
