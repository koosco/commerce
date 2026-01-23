---
name: common-core-utility
description: common-core 유틸리티 가이드. TransactionRunner, JsonUtils, @UseCase/@NotBlankIfPresent/@EnumIfPresent 어노테이션 사용이 필요할 때 사용합니다.
---

## 참조 문서

상세 문서: `common-core/docs/utilities.md`

## Quick Reference

### 1. TransactionRunner

```kotlin
import com.koosco.common.core.transaction.TransactionRunner

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val transactionRunner: TransactionRunner
) {
    // 기본 트랜잭션 (REQUIRED)
    fun createOrder(command: CreateOrderCommand): Order {
        return transactionRunner.run {
            val order = Order.create(command)
            orderRepository.save(order)
        }
    }

    // 읽기 전용 트랜잭션 (성능 최적화)
    fun getOrder(id: String): Order? {
        return transactionRunner.readOnly {
            orderRepository.findById(id)
        }
    }

    // 새 트랜잭션 (REQUIRES_NEW)
    fun logAudit(audit: Audit) {
        transactionRunner.runNew {
            auditRepository.save(audit)
        }
    }
}
```

| 메서드 | Propagation | 용도 |
|--------|-------------|------|
| `run { }` | REQUIRED | 기존 트랜잭션 참여 또는 새로 생성 |
| `readOnly { }` | REQUIRED + readOnly | 읽기 전용 최적화 |
| `runNew { }` | REQUIRES_NEW | 항상 새 트랜잭션 생성 |

### 2. JsonUtils

```kotlin
import com.koosco.common.core.util.JsonUtils

data class Order(val id: String, val amount: BigDecimal)

val order = Order("order-123", BigDecimal("10000"))

// 직렬화
val json: String? = JsonUtils.toJson(order)
val prettyJson: String? = JsonUtils.toPrettyJson(order)

// 역직렬화
val order: Order? = JsonUtils.fromJson<Order>(json)
val order: Order? = JsonUtils.fromJson(json, Order::class.java)

// 타입 변환 (Map → DTO)
val map = mapOf("id" to "order-123", "amount" to 10000)
val order: Order? = JsonUtils.convertValue<Order>(map)

// JSON 유효성 검사
val isValid: Boolean = JsonUtils.isValidJson(json)

// 공유 ObjectMapper
val objectMapper: ObjectMapper = JsonUtils.objectMapper
```

### 3. @UseCase 어노테이션

애플리케이션 레이어의 Use Case 클래스를 마킹합니다.

```kotlin
import com.koosco.common.core.annotation.UseCase

@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    fun execute(command: CreateOrderCommand): Order {
        // 비즈니스 로직
    }
}
```

### 4. @NotBlankIfPresent 어노테이션

nullable 필드가 값이 있을 때만 not blank 검증:

```kotlin
import com.koosco.common.core.annotation.NotBlankIfPresent

data class UpdateUserRequest(
    @field:NotBlankIfPresent
    val name: String?,        // null 허용, 값이 있으면 not blank

    @field:NotBlankIfPresent
    val email: String?
)
```

| 값 | 결과 |
|----|------|
| `null` | ✅ 유효 |
| `"John"` | ✅ 유효 |
| `""` | ❌ 무효 |
| `"   "` | ❌ 무효 |

### 5. @EnumIfPresent 어노테이션

nullable String이 지정된 enum 값과 일치하는지 검증:

```kotlin
import com.koosco.common.core.annotation.EnumIfPresent

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

data class UpdateOrderRequest(
    @field:EnumIfPresent(enumClass = OrderStatus::class)
    val status: String?      // null 허용, 값이 있으면 enum과 일치
)
```

| 값 | 결과 |
|----|------|
| `null` | ✅ 유효 |
| `"PENDING"` | ✅ 유효 |
| `"pending"` | ❌ 무효 (대소문자 구분) |
| `"UNKNOWN"` | ❌ 무효 |

### 6. 어노테이션 조합

```kotlin
data class UpdateProductRequest(
    @field:NotBlankIfPresent
    val name: String?,

    @field:EnumIfPresent(enumClass = ProductStatus::class)
    val status: String?,

    @field:Min(0)
    val price: BigDecimal?,

    @field:Size(max = 1000)
    @field:NotBlankIfPresent
    val description: String?
)
```

## Auto-Configuration

Auto-Configuration으로 자동 등록되는 빈:

| 빈 | 조건 | 기본값 |
|----|------|--------|
| `GlobalExceptionHandler` | Servlet 웹앱 | 활성화 |
| `ApiResponseAdvice` | Servlet 웹앱 | 비활성화 |
| `ObjectMapper` | 미정의 시 | 활성화 |
| `TransactionRunner` | 미정의 시 | 활성화 |

커스텀 구현 제공 시 Auto-Configuration이 비활성화됩니다:

```kotlin
@Configuration
class CustomConfig {
    @Bean
    fun transactionRunner(): TransactionRunner {
        return CustomTransactionRunner()
    }
}
```
