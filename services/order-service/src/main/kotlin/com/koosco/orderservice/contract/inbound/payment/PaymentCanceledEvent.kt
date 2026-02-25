package com.koosco.orderservice.contract.inbound.payment

data class PaymentCanceledEvent(
    val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val canceledAmount: Long,
    val totalCanceledAmount: Long,
    val remainingAmount: Long,
    val isFullyCanceled: Boolean,
    val currency: String = "KRW",
    val canceledAt: Long,
)
