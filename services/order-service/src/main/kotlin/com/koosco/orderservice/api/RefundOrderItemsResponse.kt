package com.koosco.orderservice.api

import com.koosco.orderservice.application.result.RefundOrderItemsResult
import com.koosco.orderservice.domain.enums.OrderStatus

data class RefundOrderItemsResponse(
    val orderId: Long,
    val refundAmount: Long,
    val refundedItemIds: List<Long>,
    val orderStatus: OrderStatus,
) {
    companion object {
        fun from(result: RefundOrderItemsResult): RefundOrderItemsResponse = RefundOrderItemsResponse(
            orderId = result.orderId,
            refundAmount = result.refundAmount,
            refundedItemIds = result.refundedItemIds,
            orderStatus = result.orderStatus,
        )
    }
}
