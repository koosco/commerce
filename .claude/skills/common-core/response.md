# common-core API Response

이 skill은 common-core의 API 응답 기능을 사용할 때 참조합니다.

## 사용 시점

- 컨트롤러에서 API 응답을 반환할 때
- 일관된 응답 포맷이 필요할 때
- ApiResponseAdvice 설정이 필요할 때

## 참조 문서

상세 문서: @common-core/docs/api-response.md

## Quick Reference

### 1. ApiResponse 사용 (권장)

```kotlin
import com.koosco.common.core.response.ApiResponse

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {
    // 데이터와 함께 성공 응답
    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: String): ApiResponse<OrderDto> {
        val order = orderService.getOrder(id)
        return ApiResponse.success(order.toDto())
    }

    // 데이터 없이 성공 응답
    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: String): ApiResponse<Unit> {
        orderService.deleteOrder(id)
        return ApiResponse.success()
    }

    // 리스트 응답
    @GetMapping
    fun getOrders(): ApiResponse<List<OrderDto>> {
        val orders = orderService.getOrders()
        return ApiResponse.success(orders.map { it.toDto() })
    }
}
```

### 2. 에러 응답 (수동)

일반적으로 예외를 throw하면 GlobalExceptionHandler가 자동 처리합니다.

```kotlin
import com.koosco.common.core.response.ApiResponse
import com.koosco.common.core.error.ApiError

// ErrorCode로 에러 응답
return ApiResponse.error<OrderDto>(CommonErrorCode.NOT_FOUND)

// 메시지와 함께
return ApiResponse.error<OrderDto>(
    errorCode = OrderErrorCode.ORDER_NOT_FOUND,
    message = "주문 ID: $id 를 찾을 수 없습니다."
)

// 필드 에러와 함께
val fieldErrors = listOf(
    ApiError.FieldError("email", "invalid", "이메일 형식 오류")
)
return ApiResponse.error<OrderDto>(
    errorCode = CommonErrorCode.VALIDATION_ERROR,
    fieldErrors = fieldErrors
)
```

### 3. 자동 응답 래핑 (선택적)

```yaml
# application.yml
common:
  core:
    response-advice:
      enabled: true  # 기본값: false
```

자동 래핑 활성화 시:

```kotlin
@RestController
class OrderController {
    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: String): OrderDto {
        // 반환값이 자동으로 ApiResponse.success(order)로 래핑됨
        return orderService.getOrder(id).toDto()
    }
}
```

### 4. 자동 래핑 제외

```kotlin
import com.koosco.common.core.response.ApiResponseIgnore

// 클래스 레벨 제외
@ApiResponseIgnore
@RestController
class HealthController { ... }

// 메서드 레벨 제외
@ApiResponseIgnore
@GetMapping("/raw")
fun rawResponse(): String = "raw"
```

## 응답 포맷

### 성공 응답

```json
{
  "success": true,
  "data": {
    "id": "order-123",
    "status": "PENDING",
    "totalAmount": 10000
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 에러 응답

```json
{
  "success": false,
  "error": {
    "code": "ORDER-404-001",
    "message": "주문을 찾을 수 없습니다.",
    "details": "주문 ID: order-123",
    "fieldErrors": [
      {
        "field": "email",
        "value": "invalid",
        "reason": "이메일 형식 오류"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 권장 패턴

예외 기반 에러 처리를 권장합니다:

```kotlin
@RestController
class OrderController(private val getOrderUseCase: GetOrderUseCase) {

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: String): ApiResponse<OrderDto> {
        // NotFoundException 발생 시 GlobalExceptionHandler가 자동 처리
        val order = getOrderUseCase.execute(id)
        return ApiResponse.success(order.toDto())
    }
}

@UseCase
class GetOrderUseCase(private val orderRepository: OrderRepository) {

    fun execute(id: String): Order {
        return orderRepository.findById(id)
            ?: throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)
    }
}
```
