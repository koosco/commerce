# common-core Event System

이 skill은 common-core의 Kafka 이벤트 발행 기능을 사용할 때 참조합니다.

## 사용 시점

- DomainEvent 정의가 필요할 때
- Kafka로 이벤트 발행이 필요할 때
- CloudEvent 스펙을 이해해야 할 때
- EventPublisher 구현이 필요할 때

## 참조 문서

상세 문서: @common-core/docs/event-system.md
CloudEvents 상세: @common-core/src/main/kotlin/com/koosco/common/core/event/README.md

## Quick Reference

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
