package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.common.core.event.IntegrationEvent

data class PaymentFailedEvent(
    val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val cancelledAmount: Long,
    val currency: String = "KRW",
    val reason: String,
    val cancelledAt: Long,
    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    override val aggregateId: String get() = paymentId

    override fun getEventType(): String = "payment.failed"

    override fun getSubject(): String = "payment/$paymentId"
}
