package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.common.core.event.IntegrationEvent

data class PaymentCompletedEvent(
    val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val paidAmount: Long,
    val currency: String = "KRW",
    val approvedAt: Long,
    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    override val aggregateId: String get() = paymentId

    override fun getEventType(): String = "payment.completed"

    override fun getSubject(): String = "payment/$paymentId"
}
