package com.koosco.orderservice.order.api

import com.koosco.orderservice.order.application.result.CreateOrderResult
import com.koosco.orderservice.order.application.result.OrderDetailResult
import com.koosco.orderservice.order.application.result.OrderItemDetailResult
import com.koosco.orderservice.order.application.result.OrderListResult
import com.koosco.orderservice.order.application.result.RefundResult
import com.koosco.orderservice.order.domain.OrderItemStatus
import com.koosco.orderservice.order.domain.OrderStatus
import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: Long,
    val discountAmount: Long,
    val payableAmount: Long,
    val refundedAmount: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(order: OrderListResult): OrderResponse = OrderResponse(
            orderId = order.orderId,
            userId = order.userId,
            status = order.status,
            totalAmount = order.totalAmount,
            discountAmount = order.discountAmount,
            payableAmount = order.payableAmount,
            refundedAmount = order.refundedAmount,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}

data class OrderItemResponse(
    val itemId: Long,
    val skuId: String,
    val quantity: Int,
    val unitPrice: Long,
    val totalPrice: Long,
    val discountAmount: Long,
    val refundableAmount: Long,
    val status: OrderItemStatus,
) {
    companion object {
        fun from(item: OrderItemDetailResult): OrderItemResponse = OrderItemResponse(
            itemId = item.itemId,
            skuId = item.skuId,
            quantity = item.quantity,
            unitPrice = item.unitPrice,
            totalPrice = item.totalPrice,
            discountAmount = item.discountAmount,
            refundableAmount = item.refundableAmount,
            status = item.status,
        )
    }
}

data class OrderDetailResponse(
    val orderId: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: Long,
    val discountAmount: Long,
    val payableAmount: Long,
    val refundedAmount: Long,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(result: OrderDetailResult): OrderDetailResponse = OrderDetailResponse(
            orderId = result.orderId,
            userId = result.userId,
            status = result.status,
            totalAmount = result.totalAmount,
            discountAmount = result.discountAmount,
            payableAmount = result.payableAmount,
            refundedAmount = result.refundedAmount,
            items = result.items.map { OrderItemResponse.from(it) },
            createdAt = result.createdAt,
            updatedAt = result.updatedAt,
        )
    }
}

data class CreateOrderResponse(val orderId: Long, val status: OrderStatus, val payableAmount: Long) {
    companion object {
        fun from(result: CreateOrderResult): CreateOrderResponse = CreateOrderResponse(
            orderId = result.orderId,
            status = result.status,
            payableAmount = result.payableAmount,
        )
    }
}

data class RefundResponse(
    val orderId: Long,
    val refundedItemIds: List<Long>,
    val totalRefundAmount: Long,
    val newStatus: OrderStatus,
) {
    companion object {
        fun from(result: RefundResult): RefundResponse = RefundResponse(
            orderId = result.orderId,
            refundedItemIds = result.refundedItemIds,
            totalRefundAmount = result.totalRefundAmount,
            newStatus = result.newStatus,
        )
    }
}
