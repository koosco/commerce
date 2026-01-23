---
name: mono-clean-arch
description: Clean Architecture 계층 구조와 의존성 규칙 가이드. 새로운 기능 개발 시 패키지 구조, 계층 간 의존성 규칙, Port/Adapter 패턴 적용이 필요할 때 사용합니다.
---

## 레이어 구조

```
{service}/src/main/kotlin/com/koosco/{servicename}/{domain}/
├── api/                    # Presentation Layer (외부)
│   ├── controller/         # REST Controllers
│   ├── request/            # Request DTOs
│   └── response/           # Response DTOs
│
├── application/            # Application Layer (내부)
│   ├── usecase/            # Use Cases (@UseCase)
│   ├── command/            # Input Commands
│   ├── result/             # Output Results
│   ├── port/               # Port Interfaces
│   │   ├── inbound/        # Inbound Ports (Use Case interfaces)
│   │   └── outbound/       # Outbound Ports (Repository interfaces)
│   └── contract/           # External Contracts
│       ├── inbound/        # 외부에서 들어오는 이벤트
│       └── outbound/       # 외부로 나가는 이벤트
│
├── domain/                 # Domain Layer (핵심)
│   ├── entity/             # Domain Entities
│   ├── vo/                 # Value Objects
│   ├── enums/              # Domain Enums
│   ├── event/              # Domain Events
│   └── exception/          # Domain Exceptions
│
└── infra/                  # Infrastructure Layer (외부)
    ├── persist/            # JPA Repositories
    │   ├── entity/         # JPA Entities
    │   ├── repository/     # Spring Data Repositories
    │   └── converter/      # Type Converters
    └── messaging/          # Kafka Integration
        └── kafka/
            ├── producer/   # Event Publishers
            └── consumer/   # Event Consumers
```

## 의존성 규칙

```
         ┌──────────────┐
         │     api      │  ← 외부 요청 진입점
         └──────┬───────┘
                │ depends on
         ┌──────▼───────┐
         │  application │  ← 비즈니스 로직 (Use Cases)
         └──────┬───────┘
                │ depends on
         ┌──────▼───────┐
         │    domain    │  ← 핵심 도메인 (엔티티, VO)
         └──────────────┘
                ▲
                │ implements (via Ports)
         ┌──────┴───────┐
         │    infra     │  ← 외부 시스템 연동
         └──────────────┘
```

**핵심 규칙:**
- `application`, `domain` → `api`, `infra` 의존 금지
- `infra` → `application` 의존 가능 (Port 구현)
- `api` → `application` 의존 가능 (Use Case 호출)

## Quick Reference

### 1. Use Case 작성

```kotlin
// application/usecase/CreateOrderUseCase.kt
import com.koosco.common.core.annotation.UseCase

@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,  // Outbound Port
    private val eventPublisher: EventPublisher,
) {
    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        // 1. 도메인 로직 실행
        val order = Order.create(
            userId = command.userId,
            items = command.items,
        )

        // 2. 저장
        val saved = orderRepository.save(order)

        // 3. 이벤트 발행
        eventPublisher.publish(OrderCreatedEvent(saved))

        // 4. 결과 반환
        return CreateOrderResult(
            orderId = saved.id,
            status = saved.status,
        )
    }
}
```

### 2. Command/Result 정의

```kotlin
// application/command/CreateOrderCommand.kt
data class CreateOrderCommand(
    val userId: String,
    val items: List<OrderItemCommand>,
)

data class OrderItemCommand(
    val productId: String,
    val quantity: Int,
)

// application/result/CreateOrderResult.kt
data class CreateOrderResult(
    val orderId: String,
    val status: OrderStatus,
)
```

### 3. Port Interface 정의

```kotlin
// application/port/outbound/OrderRepository.kt
interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: String): Order?
    fun findByUserId(userId: String): List<Order>
}
```

### 4. Adapter 구현 (Infra)

```kotlin
// infra/persist/repository/JpaOrderRepository.kt
@Repository
class JpaOrderRepository(
    private val springDataRepository: OrderJpaRepository,
) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = OrderEntity.from(order)
        val saved = springDataRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: String): Order? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }
}
```

### 5. Controller 작성 (API)

```kotlin
// api/controller/OrderController.kt
@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase,
) {
    @PostMapping
    fun createOrder(
        @AuthId userId: String,
        @RequestBody request: CreateOrderRequest,
    ): ApiResponse<CreateOrderResponse> {
        val command = request.toCommand(userId)
        val result = createOrderUseCase.execute(command)
        return ApiResponse.success(CreateOrderResponse.from(result))
    }
}
```

### 6. Domain Entity

```kotlin
// domain/entity/Order.kt
class Order private constructor(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    var status: OrderStatus,
    val createdAt: Instant,
) {
    companion object {
        fun create(userId: String, items: List<OrderItemCommand>): Order {
            require(items.isNotEmpty()) { "Order must have at least one item" }

            return Order(
                id = UUID.randomUUID().toString(),
                userId = userId,
                items = items.map { OrderItem.from(it) },
                status = OrderStatus.CREATED,
                createdAt = Instant.now(),
            )
        }
    }

    fun complete() {
        check(status == OrderStatus.PAID) { "Only paid orders can be completed" }
        status = OrderStatus.COMPLETED
    }
}
```

## 데이터 흐름

```
Request → Controller → Command → UseCase → Domain → Repository
                                               ↓
Response ← Controller ← Result ← UseCase ← Domain ← Repository
```

| 단계 | 객체 | 설명 |
|-----|------|------|
| API 진입 | `XxxRequest` | HTTP Request Body |
| 명령 변환 | `XxxCommand` | Use Case 입력 |
| 비즈니스 로직 | `Domain Entity` | 핵심 도메인 로직 |
| 영속화 | `XxxEntity` | JPA Entity |
| 결과 변환 | `XxxResult` | Use Case 출력 |
| 응답 반환 | `XxxResponse` | HTTP Response Body |

## Anti-Patterns (피해야 할 것)

```kotlin
// ❌ Application Layer에서 Infrastructure 직접 의존
@UseCase
class BadUseCase(
    private val jpaRepository: OrderJpaRepository,  // Spring Data 직접 의존!
)

// ✅ Port Interface를 통해 의존
@UseCase
class GoodUseCase(
    private val orderRepository: OrderRepository,  // Port Interface
)

// ❌ Domain에서 Framework 의존
class Order(
    @Id  // JPA 어노테이션이 Domain에!
    val id: String,
)

// ✅ Domain은 순수 Kotlin
class Order(
    val id: String,
)

// ❌ Controller에서 직접 Repository 호출
@RestController
class BadController(
    private val orderRepository: OrderRepository,  // UseCase 건너뜀!
)

// ✅ UseCase를 통해 호출
@RestController
class GoodController(
    private val createOrderUseCase: CreateOrderUseCase,
)
```
