package com.koosco.orderservice.common.error

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class PromotionErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {

    // 400 Bad Request
    INVALID_PROMOTION_REQUEST("PROMOTION-400-001", "프로모션 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_APPLICABLE("PROMOTION-400-002", "적용할 수 없는 프로모션입니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    PROMOTION_NOT_FOUND("PROMOTION-404-001", "프로모션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    PROMOTION_ALREADY_APPLIED("PROMOTION-409-001", "이미 적용된 프로모션입니다.", HttpStatus.CONFLICT),
}
