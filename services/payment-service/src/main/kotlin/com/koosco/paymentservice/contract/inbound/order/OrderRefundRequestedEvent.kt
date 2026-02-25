package com.koosco.paymentservice.contract.inbound.order

data class OrderRefundRequestedEvent(
    val orderId: Long,
    val userId: Long,
    val refundAmount: Long,
    val refundedItemIds: List<Long>,
    val correlationId: String,
    val causationId: String? = null,
)
