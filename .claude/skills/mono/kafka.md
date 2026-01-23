# mono Kafka Integration

이 skill은 Kafka Producer/Consumer 작성 및 이벤트 처리를 참조합니다.

## 사용 시점

- Kafka Consumer 작성이 필요할 때
- Kafka Producer 작성이 필요할 때
- CloudEvent 파싱이 필요할 때
- 멱등성 처리가 필요할 때

## 참조 문서

- CloudEvent 스펙: `common/common-core/src/main/kotlin/com/koosco/common/core/event/CloudEvent.kt`
- Event 관련 코드: `common/common-core/src/main/kotlin/com/koosco/common/core/event/`

## Quick Reference

### 1. Kafka Consumer 작성

```kotlin
package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.koosco.common.core.event.CloudEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentEventConsumer(
    private val objectMapper: ObjectMapper,
    private val orderService: OrderService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["payment-completed"],
        groupId = "order-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handlePaymentCompleted(message: String) {
        try {
            val cloudEvent: CloudEvent<PaymentCompletedData> = objectMapper.readValue(message)

            // 멱등성 체크 (eventId 기반)
            if (eventRepository.existsByEventId(cloudEvent.id)) {
                log.info("Duplicate event ignored: {}", cloudEvent.id)
                return
            }

            cloudEvent.data?.let { data ->
                orderService.completePayment(data.orderId, data.paymentId)
            }

            // 이벤트 처리 기록
            eventRepository.save(ProcessedEvent(cloudEvent.id))

        } catch (e: Exception) {
            log.error("Failed to process payment event", e)
            throw e  // DLQ로 전송됨
        }
    }
}

data class PaymentCompletedData(
    val orderId: String,
    val paymentId: String,
    val amount: BigDecimal,
)
```

### 2. Kafka Producer 작성

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
        // com.koosco.order.created -> order-created
        return eventType
            .substringAfter("com.koosco.")
            .replace(".", "-")
    }
}
```

### 3. AbstractEventPublisher 사용 (common-core)

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

### 4. DomainEvent 정의

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

### 5. Kafka Configuration

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

| Event Type | Topic Name |
|------------|------------|
| `com.koosco.order.created` | `order-created` |
| `com.koosco.payment.completed` | `payment-completed` |
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
