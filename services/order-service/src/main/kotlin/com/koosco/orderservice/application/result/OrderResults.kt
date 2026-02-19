package com.koosco.orderservice.application.result

import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.entity.OrderItem
import com.koosco.orderservice.domain.enums.OrderItemStatus
import com.koosco.orderservice.domain.enums.OrderStatus
import java.time.LocalDateTime

/**
 * 주문 생성
 */
data class CreateOrderResult(val orderId: Long, val status: OrderStatus, val payableAmount: Long)

/**
 * 주문 목록 조회
 */
data class OrderListResult(
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
        fun from(order: Order): OrderListResult = OrderListResult(
            orderId = order.id!!,
            userId = order.userId,
            status = order.status,
            totalAmount = order.totalAmount.amount,
            discountAmount = order.discountAmount.amount,
            payableAmount = order.payableAmount.amount,
            refundedAmount = order.refundedAmount.amount,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}

/**
 * 주문 상세 조회
 */
data class OrderDetailResult(
    val orderId: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: Long,
    val discountAmount: Long,
    val payableAmount: Long,
    val refundedAmount: Long,
    val items: List<OrderItemDetailResult>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(order: Order): OrderDetailResult = OrderDetailResult(
            orderId = order.id!!,
            userId = order.userId,
            status = order.status,
            totalAmount = order.totalAmount.amount,
            discountAmount = order.discountAmount.amount,
            payableAmount = order.payableAmount.amount,
            refundedAmount = order.refundedAmount.amount,
            items = order.items.map { OrderItemDetailResult.from(it) },
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}

data class OrderItemDetailResult(
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
        fun from(item: OrderItem): OrderItemDetailResult = OrderItemDetailResult(
            itemId = item.id!!,
            skuId = item.skuId,
            quantity = item.quantity,
            unitPrice = item.unitPrice.amount,
            totalPrice = item.totalPrice.amount,
            discountAmount = item.discountAmount.amount,
            refundableAmount = item.refundableAmount.amount,
            status = item.status,
        )
    }
}

/**
 * 주문 환불
 */
data class RefundResult(
    val orderId: Long,
    val refundedItemIds: List<Long>,
    val totalRefundAmount: Long,
    val newStatus: OrderStatus,
)
