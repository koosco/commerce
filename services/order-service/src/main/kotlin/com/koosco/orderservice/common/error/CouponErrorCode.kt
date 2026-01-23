package com.koosco.orderservice.common.error

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class CouponErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_COUPON_REQUEST("COUPON-400-001", "쿠폰 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    COUPON_NOT_APPLICABLE("COUPON-400-002", "해당 주문에 적용할 수 없는 쿠폰입니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    COUPON_NOT_FOUND("COUPON-404-001", "쿠폰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    COUPON_ALREADY_USED("COUPON-409-001", "이미 사용된 쿠폰입니다.", HttpStatus.CONFLICT),
    COUPON_EXPIRED("COUPON-409-002", "만료된 쿠폰입니다.", HttpStatus.CONFLICT),
}
