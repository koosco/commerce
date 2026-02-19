package com.koosco.orderservice.application.contract.inbound.payment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.jetbrains.annotations.NotNull

/**
 * fileName       : PaymentCancelledEvent
 * author         : koo
 * date           : 2025. 12. 23. 오전 3:33
 * description    : 결제 취소
 */
data class PaymentFailedEvent(
    @field:NotNull
    val orderId: Long,

    @field:NotBlank
    val paymentId: String,

    val transactionId: String? = null,

    /**
     * 취소/환불 금액 (부분취소면 일부 금액)
     */
    @field:PositiveOrZero
    val cancelledAmount: Long,

    @field:NotBlank
    val currency: String = "KRW",

    /**
     * 취소 사유 코드 (예: USER_CANCELLED, PAYMENT_TIMEOUT, STOCK_CONFIRM_FAILED, REFUND_REQUESTED)
     */
    @field:NotBlank
    val reason: String,

    /**
     * 취소 완료 시각 (epoch millis)
     */
    @field:Positive
    val cancelledAt: Long,

    @field:NotBlank
    val correlationId: String,

    val causationId: String? = null,
)
