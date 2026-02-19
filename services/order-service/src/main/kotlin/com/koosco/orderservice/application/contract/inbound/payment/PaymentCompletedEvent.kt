package com.koosco.orderservice.application.contract.inbound.payment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.jetbrains.annotations.NotNull

/**
 * fileName       : PaymentCompletedEvent
 * author         : koo
 * date           : 2025. 12. 23. 오전 3:34
 * description    :
 */
data class PaymentCompletedEvent(
    @field:NotNull
    val orderId: Long,

    /**
     * payment-service 내부 결제 식별자 (멱등/재처리에 매우 유용)
     */
    @field:NotBlank
    val paymentId: String,

    /**
     * PG 승인/거래 식별자 (있으면 강추, 없으면 null 허용)
     */
    val transactionId: String? = null,

    @field:PositiveOrZero
    val paidAmount: Long,

    @field:NotBlank
    val currency: String = "KRW",

    /**
     * 승인 시각 (epoch millis)
     */
    @field:Positive
    val approvedAt: Long,

    /**
     * 사가 추적용 (권장: orderId 문자열로 통일)
     */
    @field:NotBlank
    val correlationId: String,

    /**
     * 직전 원인 메시지 id (보통 payment.create.requested의 CloudEvent.id)
     */
    val causationId: String? = null,
)
