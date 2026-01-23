# CloudEvents 기반 이벤트 시스템 사용 가이드

## 개요

이 라이브러리는 CNCF CloudEvents v1.0 스펙을 준수하는 MSA용 공통 이벤트 포맷을 제공합니다.

## CloudEvents 스펙

CloudEvents는 이벤트를 일관된 방식으로 설명하기 위한 표준 사양입니다.

### 핵심 필드

#### 필수 필드
- `id`: 이벤트의 고유 식별자
- `source`: 이벤트 발생 출처 (URI 형식)
- `type`: 이벤트 타입 (역도메인 표기법 권장)
- `specversion`: CloudEvents 스펙 버전 (1.0)

#### 선택 필드
- `time`: 이벤트 발생 시각 (RFC 3339)
- `subject`: 이벤트 대상 식별자
- `datacontenttype`: 데이터의 MIME 타입
- `dataschema`: 데이터 스키마 URI
- `data`: 실제 이벤트 페이로드

## 1. CloudEvent 직접 생성

### 기본 생성

```kotlin
import com.koosco.common.core.event.CloudEvent

// 팩토리 메서드 사용
val event = CloudEvent.of(
    source = "urn:koosco:order-service",
    type = "com.koosco.order.created",
    data = OrderData(orderId = "order-123", amount = 10000),
    subject = "order-123"
)
```

### 빌더 패턴 사용

```kotlin
import com.koosco.common.core.event.CloudEventBuilder

val event = CloudEventBuilder.builder<OrderData>()
    .source("urn:koosco:order-service")
    .type("com.koosco.order.created")
    .subject("order-123")
    .data(OrderData(orderId = "order-123", amount = 10000))
    .dataSchema("https://schemas.koosco.com/order/v1")
    .build()
```

### DSL 스타일 사용

```kotlin
import com.koosco.common.core.event.cloudEvent

val event = cloudEvent<OrderData> {
    source("urn:koosco:order-service")
    type("com.koosco.order.created")
    subject("order-123")
    data(OrderData(orderId = "order-123", amount = 10000))
}
```

## 2. DomainEvent 사용 (권장)

MSA 환경에서는 DomainEvent를 사용하는 것을 권장합니다.

### DomainEvent 정의

```kotlin
import com.koosco.common.core.event.AbstractDomainEvent
import java.math.BigDecimal

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItem>
) : AbstractDomainEvent() {
    override fun getEventType(): String = "com.koosco.order.created"
    override fun getAggregateId(): String = orderId
}

// 또는 수동으로 eventId와 occurredAt 관리
data class PaymentCompletedEvent(
    val paymentId: String,
    val orderId: String,
    val amount: BigDecimal,
    override val eventId: String = CloudEvent.generateId(),
    override val occurredAt: Instant = Instant.now()
) : DomainEvent {
    override fun getEventType(): String = "com.koosco.payment.completed"
    override fun getAggregateId(): String = paymentId
}
```

### DomainEvent를 CloudEvent로 변환

```kotlin
val domainEvent = OrderCreatedEvent(
    orderId = "order-123",
    userId = "user-456",
    totalAmount = BigDecimal("50000"),
    items = listOf(...)
)

// 방법 1: 직접 변환
val cloudEvent = domainEvent.toCloudEvent(
    source = "urn:koosco:order-service",
    dataSchema = "https://schemas.koosco.com/order/v1"
)

// 방법 2: Extension function으로 간편하게
val cloudEvent = domainEvent.toCloudEventWithPrefix("koosco:order-service")
// source가 자동으로 "urn:koosco:order-service"로 변환됨
```

## 3. 이벤트 발행

### EventPublisher 구현

```kotlin
import com.koosco.common.core.event.EventPublisher
import com.koosco.common.core.event.CloudEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>
) : EventPublisher {

    override fun <T> publish(event: CloudEvent<T>) {
        val json = EventUtils.toJson(event)
        kafkaTemplate.send("events", event.type, json)
    }

    // 배치 발행 최적화
    override fun <T> publishBatch(events: List<CloudEvent<T>>) {
        events.forEach { event ->
            publish(event)
        }
    }
}
```

### 이벤트 발행 사용 예시

```kotlin
@Service
class OrderService(
    private val eventPublisher: EventPublisher
) {
    fun createOrder(request: CreateOrderRequest): Order {
        // 주문 생성 로직
        val order = orderRepository.save(...)

        // 도메인 이벤트 생성
        val event = OrderCreatedEvent(
            orderId = order.id,
            userId = order.userId,
            totalAmount = order.totalAmount,
            items = order.items
        )

        // 이벤트 발행
        eventPublisher.publishDomainEvent(event, "urn:koosco:order-service")

        return order
    }
}
```

## 4. 이벤트 처리

### EventHandler 구현

```kotlin
import com.koosco.common.core.event.EventHandler
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventHandler : EventHandler<OrderCreatedEvent> {

    override fun handle(event: OrderCreatedEvent) {
        // 이벤트 처리 로직
        println("Order created: ${event.orderId}")
        // 재고 감소, 알림 발송 등
    }

    override fun canHandle(eventType: String): Boolean {
        return eventType == "com.koosco.order.created"
    }

    override fun getOrder(): Int = 0
}
```

### CloudEvent 직접 처리

```kotlin
import com.koosco.common.core.event.CloudEventHandler

@Component
class OrderEventCloudHandler : CloudEventHandler<OrderCreatedEvent> {

    override fun handle(event: CloudEvent<OrderCreatedEvent>) {
        // CloudEvent 메타데이터 활용
        println("Event ID: ${event.id}")
        println("Source: ${event.source}")
        println("Time: ${event.time}")

        // 실제 데이터 처리
        event.data?.let { orderEvent ->
            println("Order ID: ${orderEvent.orderId}")
        }
    }

    override fun canHandle(eventType: String): Boolean {
        return eventType == "com.koosco.order.created"
    }
}
```

## 5. 이벤트 검증

### 유효성 검증

```kotlin
import com.koosco.common.core.event.EventValidator
import com.koosco.common.core.event.EventUtils

// CloudEvent 검증
val event = CloudEvent.of(...)
val validationResult = EventValidator.validate(event)

if (validationResult.isValid) {
    // 유효한 이벤트
    eventPublisher.publish(event)
} else {
    // 검증 실패
    println("Validation errors: ${validationResult.errors}")
}

// 예외를 던지는 방식
try {
    validationResult.throwIfInvalid()
    eventPublisher.publish(event)
} catch (e: ValidationException) {
    println("Invalid event: ${e.message}")
}

// DomainEvent 검증
val domainEvent = OrderCreatedEvent(...)
val result = EventValidator.validate(domainEvent)
```

### 검증과 직렬화 통합

```kotlin
// 검증 후 직렬화
val json = EventUtils.validateAndSerialize(event)

// 역직렬화 후 검증
val event = EventUtils.deserializeAndValidate(json, OrderData::class.java)
```

## 6. 이벤트 직렬화/역직렬화

### JSON 변환

```kotlin
import com.koosco.common.core.event.EventUtils

// CloudEvent → JSON
val json = EventUtils.toJson(event)

// JSON → CloudEvent
val event = EventUtils.fromJson<OrderData>(json)

// 또는 클래스 지정
val event = EventUtils.fromJson(json, OrderData::class.java)
```

### Map 변환

```kotlin
// CloudEvent → Map
val map = EventUtils.toMap(event)

// Map → CloudEvent
val event = EventUtils.fromMap(map, OrderData::class.java)
```

## 7. PublishableDomainEvent

외부 시스템에 발행해야 하는 이벤트를 명시적으로 표시할 수 있습니다.

```kotlin
import com.koosco.common.core.event.PublishableDomainEvent

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal
) : AbstractDomainEvent(), PublishableDomainEvent {
    override fun getEventType(): String = "com.koosco.order.created"
    override fun getAggregateId(): String = orderId
}

// 필터링하여 발행
if (event is PublishableDomainEvent) {
    eventPublisher.publishDomainEvent(event, source)
}
```

## 8. 실전 예제

### 주문 서비스 전체 예제

```kotlin
// 1. 도메인 이벤트 정의
data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItem>
) : AbstractDomainEvent(), PublishableDomainEvent {
    override fun getEventType(): String = "com.koosco.order.created"
    override fun getAggregateId(): String = orderId
}

// 2. 서비스에서 이벤트 발행
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    fun createOrder(request: CreateOrderRequest): Order {
        val order = Order.create(request)
        orderRepository.save(order)

        val event = OrderCreatedEvent(
            orderId = order.id,
            userId = order.userId,
            totalAmount = order.totalAmount,
            items = order.items
        )

        eventPublisher.publishDomainEvent(event, "urn:koosco:order-service")

        return order
    }
}

// 3. 다른 서비스에서 이벤트 수신
@Component
class InventoryEventHandler : EventHandler<OrderCreatedEvent> {

    override fun handle(event: OrderCreatedEvent) {
        event.items.forEach { item ->
            inventoryService.decreaseStock(item.productId, item.quantity)
        }
    }

    override fun canHandle(eventType: String): Boolean {
        return eventType == "com.koosco.order.created"
    }
}
```

## 9. 베스트 프랙티스

### 이벤트 타입 네이밍

```kotlin
// 권장: 역도메인 표기법
"com.koosco.order.created"
"com.koosco.payment.completed"
"com.koosco.inventory.stock-decreased"

// 지양
"OrderCreated"
"order-created"
"CREATE_ORDER"
```

### Source URI 형식

```kotlin
// URN 형식 (권장)
"urn:koosco:order-service"
"urn:koosco:payment-service"

// HTTP URL 형식
"https://api.koosco.com/orders"
"https://api.koosco.com/payments"
```

### 이벤트 버전 관리

```kotlin
data class OrderCreatedEventV2(
    // 새 필드 추가
    val shippingAddress: Address,
    // 기존 필드
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal
) : AbstractDomainEvent() {
    override fun getEventType(): String = "com.koosco.order.created"
    override fun getEventVersion(): String = "2.0"
    override fun getAggregateId(): String = orderId
}
```

## 10. 참고 자료

- [CloudEvents 공식 스펙](https://github.com/cloudevents/spec/blob/v1.0.2/cloudevents/spec.md)
- [CNCF CloudEvents](https://cloudevents.io/)