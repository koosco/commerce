# Toss Payments 연동

Payment Service에서 Toss Payments API와 통합하는 방법을 설명합니다.

## 결제 승인 플로우

```
사용자
  → Frontend (Toss Widget)
    → WidgetController.confirmPayment()
      → ApprovePaymentUseCase
        → PaymentGateway.approve() (TossPaymentGateway)
          ├─ 성공: Payment.approve() → PaymentCompletedEvent 발행
          └─ 실패: Payment.fail() → PaymentFailedEvent 발행
```

1. 사용자가 Toss Widget에서 결제를 완료하면, 프론트엔드가 `POST /api/payments/confirm` API를 호출합니다.
2. `WidgetController`가 요청을 수신하여 `ApprovePaymentUseCase`를 실행합니다.
3. UseCase 내부에서 `PaymentGateway.approve()`를 호출하여 Toss API에 결제 승인을 요청합니다.
4. 승인 결과에 따라 `PaymentCompletedEvent` 또는 `PaymentFailedEvent`를 발행합니다.

## PaymentGateway 인터페이스

외부 결제 게이트웨이와의 통합을 추상화하는 Port 인터페이스입니다.

```kotlin
interface PaymentGateway {
    fun approve(paymentKey: String, orderId: String, amount: Long): PaymentApprovalResult
}
```

### PaymentApprovalResult sealed class

결제 승인 결과를 표현하는 sealed class로, 성공과 실패를 타입 안전하게 처리합니다.

```kotlin
sealed class PaymentApprovalResult {
    data class Success(val transactionId: String) : PaymentApprovalResult()
    data class Failure(val reason: String, val code: String) : PaymentApprovalResult()
}
```

- **Success**: PG사에서 반환한 `transactionId`를 포함합니다.
- **Failure**: 실패 사유(`reason`)와 PG사 오류 코드(`code`)를 포함합니다.

## TossPaymentGateway 구현체

`PaymentGateway` 인터페이스의 Toss Payments API 구현체입니다.

```kotlin
@Component
class TossPaymentGateway(
    private val tossClient: TossClient,
) : PaymentGateway {

    override fun approve(paymentKey: String, orderId: String, amount: Long): PaymentApprovalResult {
        return try {
            val response = tossClient.confirmPayment(paymentKey, orderId, amount)
            PaymentApprovalResult.Success(transactionId = response.transactionId)
        } catch (e: TossApiException) {
            PaymentApprovalResult.Failure(
                reason = e.message ?: "Unknown error",
                code = e.errorCode,
            )
        }
    }
}
```

### 동작 방식

1. `TossClient`를 통해 Toss Payments 결제 승인 API를 호출합니다.
2. 성공 시 `PaymentApprovalResult.Success`를 반환합니다.
3. `TossApiException` 발생 시 `PaymentApprovalResult.Failure`로 변환하여 반환합니다.

## 주요 고려사항

### Secret Key 관리

- 개발 환경에서는 테스트 키를 사용합니다.
- 운영 환경에서는 환경변수로 Secret Key를 관리합니다.
- Secret Key가 코드에 직접 포함되지 않도록 주의해야 합니다.

### API 에러 처리

- PG사에서 반환하는 오류는 `PaymentErrorCode.PAYMENT_GATEWAY_ERROR` (502 Bad Gateway)로 변환합니다.
- `TossApiException`을 catch하여 `PaymentApprovalResult.Failure`로 매핑합니다.
- 비즈니스 로직에서 Failure 결과를 받으면 `Payment.fail()`을 호출하고 `PaymentFailedEvent`를 발행합니다.

### 타임아웃 처리

- `RestClient`에 연결 타임아웃과 읽기 타임아웃을 설정합니다.
- 타임아웃 발생 시 적절한 오류 처리가 수행됩니다.

### 재시도 로직

- 일시적 네트워크 오류 등에 대한 재시도 전략을 적용합니다.
- 멱등하지 않은 요청에 대해서는 재시도 시 중복 결제가 발생하지 않도록 주의해야 합니다.

### Port/Adapter 분리

- `PaymentGateway`는 application 계층에서 정의된 Port 인터페이스입니다.
- `TossPaymentGateway`는 infra 계층의 Adapter 구현체입니다.
- 이 분리를 통해 다음을 달성합니다:
  - **테스트 용이성**: Mock 구현으로 단위 테스트 가능
  - **Clean Architecture 유지**: application/domain 계층이 외부 PG사에 의존하지 않음
  - **PG사 교체 용이**: 인터페이스 구현체만 변경하면 PG사 전환 가능

## Toss Widget 통합 패턴

### 전체 흐름

1. **프론트엔드**: Toss Payments Widget SDK를 로드하여 결제 UI를 렌더링합니다.
2. **사용자 결제**: 사용자가 Widget에서 결제 수단을 선택하고 결제를 진행합니다.
3. **Widget 콜백**: 결제 완료 시 Widget이 `paymentKey`, `orderId`, `amount`를 콜백으로 전달합니다.
4. **서버 승인 요청**: 프론트엔드가 콜백 데이터를 `POST /api/payments/confirm`으로 전송합니다.
5. **서버 처리**: `ApprovePaymentUseCase`가 Toss API에 최종 승인을 요청하고 결과를 처리합니다.

### 파일 위치

| 파일 | 역할 |
|------|------|
| `api/WidgetController` | Toss Widget 콜백 처리 Controller |
| `api/PaymentConfirmRequest` | 결제 승인 요청 DTO |
| `application/usecase/ApprovePaymentUseCase` | 결제 승인 UseCase |
| `infra/client/PaymentGateway` | PG사 통합 Port 인터페이스 |
| `infra/client/TossPaymentGateway` | Toss API Adapter 구현체 |
