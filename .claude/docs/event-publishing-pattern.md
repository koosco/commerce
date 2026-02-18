# Integration Event Publishing Pattern

모든 서비스는 다음 표준 패턴을 따릅니다.

## 디렉토리 구조

```
services/{service}/
├── application/
│   ├── port/
│   │   └── IntegrationEventProducer.kt    # Port 인터페이스
│   └── contract/
│       └── {Service}IntegrationEvent.kt    # 이벤트 계약
└── infra/
    └── messaging/kafka/producer/
        └── OutboxIntegrationEventProducer.kt  # Kafka 어댑터 (Outbox 패턴)
```

## 네이밍 컨벤션

- Port: `IntegrationEventProducer` (Port 접미사 사용 금지)
- Adapter: `OutboxIntegrationEventProducer` (Adapter 접미사 사용 금지)

## 발행 패턴

```kotlin
@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,
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

## 핵심 원칙

1. `@Transactional` 메서드 내에서 이벤트 발행
2. `correlationId`: 주문 ID 등 비즈니스 식별자
3. `causationId`: UUID로 생성 (이벤트 추적용)
4. CloudEvent 표준 준수 (`common-core` 활용)

## 관련 제약사항

- **Event publishing naming**: Port는 `IntegrationEventProducer`, Adapter는 `OutboxIntegrationEventProducer`로 통일
- **No Domain Event extraction pattern**: `pullDomainEvents()` 패턴 사용 금지, Integration Event 직접 발행
