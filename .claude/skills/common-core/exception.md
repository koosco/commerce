# common-core Exception Handling

이 skill은 common-core의 예외 처리 기능을 사용할 때 참조합니다.

## 사용 시점

- 도메인별 에러 코드 정의가 필요할 때
- 예외를 발생시켜야 할 때
- GlobalExceptionHandler의 동작을 이해해야 할 때

## 참조 문서

상세 문서: @common-core/docs/exception-handling.md

## Quick Reference

### 1. 도메인 에러 코드 정의

```kotlin
import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class OrderErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    ORDER_NOT_FOUND("ORDER-404-001", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("ORDER-400-001", "재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ORDER("ORDER-409-001", "이미 존재하는 주문입니다.", HttpStatus.CONFLICT),
}
```

### 2. 예외 발생

```kotlin
import com.koosco.common.core.exception.*

// 404 Not Found
throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

// 400 Bad Request
throw BadRequestException(OrderErrorCode.INSUFFICIENT_STOCK)

// 409 Conflict
throw ConflictException(OrderErrorCode.DUPLICATE_ORDER)

// 커스텀 메시지 추가
throw NotFoundException(
    errorCode = OrderErrorCode.ORDER_NOT_FOUND,
    message = "주문 ID: $orderId 를 찾을 수 없습니다."
)
```

### 3. 예외 계층구조

| 예외 클래스 | HTTP Status | 용도 |
|------------|-------------|------|
| `BadRequestException` | 400 | 잘못된 요청 |
| `ValidationException` | 400 | 유효성 검사 실패 |
| `UnauthorizedException` | 401 | 인증 필요 |
| `ForbiddenException` | 403 | 권한 없음 |
| `NotFoundException` | 404 | 리소스 없음 |
| `ConflictException` | 409 | 충돌 |
| `InternalServerException` | 500 | 서버 오류 |
| `ExternalServiceException` | 502 | 외부 서비스 오류 |
| `ServiceUnavailableException` | 503 | 서비스 불가 |

### 4. 필드 에러 포함

```kotlin
import com.koosco.common.core.error.ApiError

val fieldErrors = listOf(
    ApiError.FieldError("email", "invalid", "이메일 형식 오류"),
    ApiError.FieldError("name", "", "이름은 필수입니다")
)

throw ValidationException(
    message = "입력값 검증 실패",
    fieldErrors = fieldErrors
)
```

### 5. GlobalExceptionHandler

Auto-Configuration으로 자동 등록됩니다. 다음을 자동 처리합니다:

- `BaseException` 및 하위 클래스
- `@Valid`, `@Validated` 검증 실패
- 타입 불일치, 누락된 파라미터, 잘못된 JSON
- 예측 불가능한 오류

## 에러 응답 형식

```json
{
  "success": false,
  "error": {
    "code": "ORDER-404-001",
    "message": "주문을 찾을 수 없습니다.",
    "details": "주문 ID: order-123 를 찾을 수 없습니다.",
    "fieldErrors": [...]
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```
