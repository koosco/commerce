package com.koosco.paymentservice.common

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Payment service specific error codes.
 */
enum class PaymentErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_PAYMENT_AMOUNT("PAYMENT-400-001", "결제 금액이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS("PAYMENT-400-002", "결제 상태가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_READY("PAYMENT-400-003", "결제가 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_REQUEST("PAYMENT-400-004", "결제 요청 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    PAYMENT_NOT_FOUND("PAYMENT-404-001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    DUPLICATE_PAYMENT_REQUEST("PAYMENT-409-001", "중복된 결제 요청입니다.", HttpStatus.CONFLICT),
    PAYMENT_ALREADY_APPROVED("PAYMENT-409-002", "이미 승인된 결제입니다.", HttpStatus.CONFLICT),
    PAYMENT_ALREADY_CANCELED("PAYMENT-409-003", "이미 취소된 결제입니다.", HttpStatus.CONFLICT),

    // 422 Unprocessable Entity
    PAYMENT_APPROVAL_FAILED("PAYMENT-422-001", "결제 승인에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    PAYMENT_CANCEL_FAILED("PAYMENT-422-002", "결제 취소에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY),

    // 502 Bad Gateway (외부 PG 오류)
    PAYMENT_GATEWAY_ERROR("PAYMENT-502-001", "결제 게이트웨이 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
}
