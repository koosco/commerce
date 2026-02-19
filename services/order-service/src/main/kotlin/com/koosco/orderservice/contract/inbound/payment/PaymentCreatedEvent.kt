package com.koosco.orderservice.contract.inbound.payment

/**
 * fileName       : PaymentCreatedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:24
 * description    :
 */
data class PaymentCreatedEvent(
    val paymentId: String,
    val orderId: Long,

    val correlationId: String,
    val causationId: String?,
)
