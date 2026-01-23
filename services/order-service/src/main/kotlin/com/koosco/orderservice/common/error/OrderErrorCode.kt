package com.koosco.orderservice.common.error

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class OrderErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_ORDER_REQUEST("ORDER-400-001", "주문 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS("ORDER-400-002", "현재 주문 상태에서는 해당 작업을 수행할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_MONEY_STATUS(
        "ORDER-400-003",
        "금액이 올바르지 않습니다. 금액은 0원 이상이어야 합니다.",
        HttpStatus.BAD_REQUEST,
    ),
    INVALID_REFUND_REQUEST("ORDER-400-004", "환불 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS("ORDER-400-005", "알 수 없는 결제 상태입니다.", HttpStatus.BAD_REQUEST),

    // 403 Forbidden
    ORDER_ACCESS_DENIED("ORDER-403-001", "해당 주문에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 404 Not Found
    ORDER_NOT_FOUND("ORDER-404-001", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_FOUND("ORDER-404-002", "주문 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    ORDER_ALREADY_PAID("ORDER-409-001", "이미 결제가 완료된 주문입니다.", HttpStatus.CONFLICT),
    ORDER_ALREADY_CANCELED("ORDER-409-002", "이미 취소된 주문입니다.", HttpStatus.CONFLICT),
    ORDER_ALREADY_REFUNDED("ORDER-409-003", "이미 환불 처리된 주문입니다.", HttpStatus.CONFLICT),
    PAYMENT_AMOUNT_MISMATCH("ORDER-409-004", "결제 금액이 일치하지 않습니다.", HttpStatus.CONFLICT),
}
