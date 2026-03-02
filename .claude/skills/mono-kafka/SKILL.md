---
name: mono-kafka
description: Kafka Producer/Consumer 작성 및 이벤트 처리 가이드. Kafka Consumer/Producer 작성, CloudEvent 파싱, 멱등성 처리가 필요할 때 사용합니다.
---

## 참조 소스코드

- CloudEvent 스펙: `common/common-core/src/main/kotlin/com/koosco/common/core/event/CloudEvent.kt`
- IntegrationEvent 인터페이스: `common/common-core/src/main/kotlin/com/koosco/common/core/event/IntegrationEvent.kt`
- IntegrationEventProducer 포트: `common/common-core/src/main/kotlin/com/koosco/common/core/event/IntegrationEventProducer.kt`
- TopicResolver 인터페이스: `common/common-core/src/main/kotlin/com/koosco/common/core/event/TopicResolver.kt`
- Event 관련 코드: `common/common-core/src/main/kotlin/com/koosco/common/core/event/`
- MessageContext:
  `common/common-core/src/main/kotlin/com/koosco/common/core/messaging/MessageContext.kt`

---

## Integration Event Publishing Pattern

### 디렉토리 구조

```
common/common-core/
└── event/
    ├── IntegrationEvent.kt              # 공통 인터페이스 (common-core)
    ├── IntegrationEventProducer.kt      # Port 인터페이스 (common-core)
    └── TopicResolver.kt                 # Topic 매핑 인터페이스 (common-core)

services/{service}/
├── application/
│   └── contract/
│       └── outbound/                    # Concrete event 정의
└── infra/
    └── messaging/kafka/producer/
        └── OutboxIntegrationEventProducer.kt  # Kafka 어댑터 (Outbox 패턴)
```

### 네이밍 컨벤션

- Port: `IntegrationEventProducer` (Port 접미사 사용 금지)
- Adapter: `OutboxIntegrationEventProducer` (Adapter 접미사 사용 금지)

### 발행 패턴

```kotlin
import com.koosco.common.core.event.IntegrationEventProducer

@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,  // common-core 인터페이스
) {
    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        val savedOrder = orderRepository.save(order)

        // Integration Event 직접 생성 및 발행
        integrationEventProducer.publish(
            OrderPlacedEvent(
                orderId = savedOrder.id!!,
                userId = savedOrder.userId,
                correlationId = savedOrder.id.toString(),
                causationId = UUID.randomUUID().toString(),
            ),
        )

        return CreateOrderResult(savedOrder.id!!)
    }
}
```

### 핵심 원칙

1. `@Transactional` 메서드 내에서 이벤트 발행
2. `correlationId`: 주문 ID 등 비즈니스 식별자
3. `causationId`: UUID로 생성 (이벤트 추적용)
4. CloudEvent 표준 준수 (`common-core` 활용)
5. `pullDomainEvents()` 패턴 사용 금지, Integration Event 직접 발행

---

## Integration Event Consuming Pattern

### 디렉토리 구조

```
services/{service}/
├── application/
│   └── contract/
│       └── inbound/
│           └── {Source}Event.kt    # 수신 이벤트 DTO
└── infra/
    └── messaging/kafka/consumer/
        └── Kafka{EventName}Consumer.kt
```

### Consumer 표준 패턴

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

### 핵심 규칙

1. 클래스에 `@Validated`, 파라미터에 `@Valid` 필수
2. `groupId`는 property 참조 (`${spring.kafka.consumer.group-id}`), hardcoding 금지
3. 수동 ack 모드 (`MANUAL_IMMEDIATE`)
4. poison message는 ack 후 skip
5. 비즈니스 예외와 인프라 예외 구분 처리

### Consumer Quick Reference

```kotlin
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

---

## Producer Quick Reference

### Kafka Producer 작성

```kotlin
package com.koosco.orderservice.order.infra.messaging.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publishOrderCreated(event: OrderCreatedEvent) {
        val cloudEvent = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = event,
            subject = event.orderId,  // 파티션 키로 사용됨
        )

        val payload = objectMapper.writeValueAsString(cloudEvent)
        val topic = resolveTopicFromEventType(cloudEvent.type)

        kafkaTemplate.send(topic, cloudEvent.subject, payload)
    }

    private fun resolveTopicFromEventType(eventType: String): String {
        return eventType
            .substringAfter("com.koosco.")
            .replace(".", "-")
    }
}
```

### AbstractEventPublisher 사용 (common-core)

```kotlin
import com.koosco.common.core.event.AbstractEventPublisher
import com.koosco.common.core.event.CloudEvent

@Component
class KafkaEventPublisher(
    objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : AbstractEventPublisher(objectMapper) {

    override fun publishRaw(topic: String, key: String?, payload: String) {
        kafkaTemplate.send(topic, key, payload).get()
    }

    override fun resolveTopic(event: CloudEvent<*>): String {
        return event.type.substringAfter("com.koosco.").replace(".", "-")
    }

    override fun resolveKey(event: CloudEvent<*>): String? {
        return event.subject
    }
}
```

### DomainEvent 정의

```kotlin
import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.common.core.event.PublishableDomainEvent

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val items: List<OrderItemData>,
    val totalAmount: BigDecimal,
) : AbstractDomainEvent(), PublishableDomainEvent {

    override fun getEventType(): String = "com.koosco.order.created"
    override fun getAggregateId(): String = orderId
}
```

---

## Kafka Configuration

```kotlin
@Configuration
class KafkaConfig {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>,
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            this.consumerFactory = consumerFactory
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        }
    }
}
```

## Topic 명명 규칙

| Event Type                             | Topic Name                  |
|----------------------------------------|-----------------------------|
| `com.koosco.order.created`             | `order-created`             |
| `com.koosco.payment.completed`         | `payment-completed`         |
| `com.koosco.inventory.stock-decreased` | `inventory-stock-decreased` |

## 멱등성 처리 패턴

```kotlin
// 1. Event ID 기반 중복 체크
@Entity
class ProcessedEvent(
    @Id
    val eventId: String,
    val processedAt: Instant = Instant.now(),
)

// 2. Consumer에서 체크
if (processedEventRepository.existsById(cloudEvent.id)) {
    log.info("Duplicate event: {}", cloudEvent.id)
    return
}

// 3. 처리 후 저장
processedEventRepository.save(ProcessedEvent(cloudEvent.id))
```

## application.yaml 설정

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-service
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

## CloudEvent 응답 형식

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "source": "urn:koosco:order-service",
  "specversion": "1.0",
  "type": "com.koosco.order.created",
  "datacontenttype": "application/json",
  "subject": "order-123",
  "time": "2024-01-15T10:30:00Z",
  "data": {
    "orderId": "order-123",
    "userId": "user-456",
    "totalAmount": 10000
  }
}
```

---

## Service Event Matrix

### Event Publishing by Service

| 서비스               | Kafka 발행 | 패턴                      | 비고                            |
|-------------------|----------|-------------------------|-------------------------------|
| order-service     | O        | Integration Event 직접 발행 | `@Transactional` 내 발행         |
| inventory-service | O        | Integration Event 직접 발행 | 비트랜잭셔널 (Redis 특성)             |
| payment-service   | O        | Integration Event 직접 발행 | 멱등성 저장소 사용                    |
| catalog-service   | O        | Integration Event 직접 발행 | 표준 패턴                         |
| user-service      | X        | -                       | Feign 동기 호출 (auth-service 연동) |
| auth-service      | X        | -                       | 순수 CRUD                       |

### Event Consuming by Service

| 서비스               | 소비 이벤트                                                                                   | 멱등성            | 비고                          |
|-------------------|------------------------------------------------------------------------------------------|----------------|-----------------------------|
| order-service     | PaymentCreated/Completed/Failed, StockReserved/ReservationFailed/Confirmed/ConfirmFailed | 상태 전이 + DB 멱등성 | 7개 Consumer                 |
| inventory-service | OrderPlaced/Confirmed/Cancelled, ProductSkuCreated                                       | 상태 전이          | 4개 Consumer                 |
| payment-service   | OrderPlaced                                                                              | **DB 멱등성**     | IdempotencyRepository 사용    |
| catalog-service   | StockDepleted/StockRestored                                                              | 상태 전이 (멱등)     | 1개 Consumer (2 event types) |

### Event Flow Summary

```
order-service (Producer)
    ├── OrderPlaced → inventory-service, payment-service
    ├── OrderConfirmed → inventory-service
    └── OrderCancelled → inventory-service

inventory-service (Producer)
    ├── StockReserved → order-service
    ├── StockReservationFailed → order-service
    ├── StockConfirmed → order-service
    ├── StockConfirmFailed → order-service
    ├── StockDepleted → catalog-service
    └── StockRestored → catalog-service

payment-service (Producer)
    ├── PaymentCreated → order-service
    ├── PaymentCompleted → order-service
    └── PaymentFailed → order-service

catalog-service (Producer)
    └── ProductSkuCreated → inventory-service
```
