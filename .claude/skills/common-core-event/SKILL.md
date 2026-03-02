---
name: common-core-event
description: common-core Kafka 이벤트 발행 가이드. DomainEvent 정의, CloudEvent 스펙, EventPublisher 구현이 필요할 때 사용합니다.
---

## 참조 문서

- 상세 문서: `common-core/docs/event-system.md`
- CloudEvents 상세: `common-core/src/main/kotlin/com/koosco/common/core/event/README.md`

## Quick Reference

### 0. IntegrationEvent 인터페이스 (common-core)

```kotlin
import com.koosco.common.core.event.IntegrationEvent

// IntegrationEvent: 서비스 간 통합 이벤트 공통 인터페이스
interface IntegrationEvent {
    val aggregateId: String
    fun getEventType(): String
    fun getPartitionKey(): String = aggregateId
    fun getSubject(): String
    fun toCloudEvent(source: String): CloudEvent<out IntegrationEvent>
}

// Concrete event 예시
data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()
    override fun getEventType(): String = "order.placed"
    override fun getSubject(): String = "order/$orderId"
}
```

### 0-1. IntegrationEventProducer 포트 (common-core)

```kotlin
import com.koosco.common.core.event.IntegrationEventProducer

// 모든 서비스에서 공통으로 사용하는 이벤트 발행 포트
interface IntegrationEventProducer {
    fun publish(event: IntegrationEvent)
}
```

### 0-2. TopicResolver 인터페이스 (common-core)

```kotlin
import com.koosco.common.core.event.TopicResolver

// 이벤트 타입 → Kafka 토픽 매핑
interface TopicResolver {
    fun resolve(event: IntegrationEvent): String
}
```

### 1. DomainEvent 정의

```kotlin
import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.common.core.event.PublishableDomainEvent
import java.math.BigDecimal

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItem>
) : AbstractDomainEvent(), PublishableDomainEvent {

    override fun getEventType(): String = "com.koosco.order.created"
    override fun getAggregateId(): String = orderId
}
```

### 2. 이벤트 발행

```kotlin
@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    fun execute(command: CreateOrderCommand): Order {
        val order = Order.create(command)
        orderRepository.save(order)

        val event = OrderCreatedEvent(
            orderId = order.id,
            userId = order.userId,
            totalAmount = order.totalAmount,
            items = order.items
        )

        eventPublisher.publishDomainEvent(
            event = event,
            source = "urn:koosco:order-service"
        )

        return order
    }
}
```

### 3. EventPublisher 구현 (Kafka)

```kotlin
import com.koosco.common.core.event.AbstractEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>
) : AbstractEventPublisher(objectMapper) {

    override fun publishRaw(topic: String, key: String?, payload: String) {
        kafkaTemplate.send(topic, key, payload).get()
    }

    override fun resolveTopic(event: CloudEvent<*>): String {
        // com.koosco.order.created -> order-created
        return event.type.substringAfter("com.koosco.").replace(".", "-")
    }

    override fun resolveKey(event: CloudEvent<*>): String? {
        return event.subject  // 파티션 키로 사용
    }
}
```

### 4. CloudEvent 직접 생성

```kotlin
import com.koosco.common.core.event.CloudEvent

// 팩토리 메서드
val event = CloudEvent.of(
    source = "urn:koosco:order-service",
    type = "com.koosco.order.created",
    data = OrderData(orderId = "order-123"),
    subject = "order-123"
)

// 빌더 패턴
val event = CloudEventBuilder.builder<OrderData>()
    .source("urn:koosco:order-service")
    .type("com.koosco.order.created")
    .subject("order-123")
    .data(OrderData(orderId = "order-123"))
    .build()
```

### 5. 이벤트 핸들러 구현

```kotlin
import com.koosco.common.core.event.EventHandler
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventHandler(
    private val inventoryService: InventoryService
) : EventHandler<OrderCreatedEvent> {

    override fun handle(event: OrderCreatedEvent) {
        event.items.forEach { item ->
            inventoryService.decreaseStock(item.productId, item.quantity)
        }
    }

    override fun canHandle(eventType: String): Boolean {
        return eventType == "com.koosco.order.created"
    }

    override fun getOrder(): Int = 0
}
```

## 이벤트 타입 네이밍 규칙

```kotlin
// 권장: 역도메인 표기법
"com.koosco.order.created"
"com.koosco.payment.completed"
"com.koosco.inventory.stock-decreased"

// Source URI 형식
"urn:koosco:order-service"
"urn:koosco:payment-service"
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
