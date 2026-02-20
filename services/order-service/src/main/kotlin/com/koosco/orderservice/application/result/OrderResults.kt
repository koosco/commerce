package com.koosco.orderservice.application.result

import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.entity.OrderItem
import com.koosco.orderservice.domain.enums.OrderStatus
import java.time.LocalDateTime

/**
 * 주문 생성
 */
data class CreateOrderResult(val orderId: Long, val orderNo: String, val status: OrderStatus, val totalAmount: Long)

/**
 * 주문 목록 조회
 */
data class OrderListResult(
    val orderId: Long,
    val orderNo: String,
    val userId: Long,
    val status: OrderStatus,
    val subtotalAmount: Long,
    val discountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
    val currency: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(order: Order): OrderListResult = OrderListResult(
            orderId = order.id!!,
            orderNo = order.orderNo,
            userId = order.userId,
            status = order.status,
            subtotalAmount = order.subtotalAmount.amount,
            discountAmount = order.discountAmount.amount,
            shippingFee = order.shippingFee.amount,
            totalAmount = order.totalAmount.amount,
            currency = order.currency,
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
    val orderNo: String,
    val userId: Long,
    val status: OrderStatus,
    val subtotalAmount: Long,
    val discountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
    val currency: String,
    val shippingAddressSnapshot: String,
    val pricingSnapshot: String?,
    val items: List<OrderItemDetailResult>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val placedAt: LocalDateTime?,
    val paidAt: LocalDateTime?,
    val canceledAt: LocalDateTime?,
) {
    companion object {
        fun from(order: Order): OrderDetailResult = OrderDetailResult(
            orderId = order.id!!,
            orderNo = order.orderNo,
            userId = order.userId,
            status = order.status,
            subtotalAmount = order.subtotalAmount.amount,
            discountAmount = order.discountAmount.amount,
            shippingFee = order.shippingFee.amount,
            totalAmount = order.totalAmount.amount,
            currency = order.currency,
            shippingAddressSnapshot = order.shippingAddressSnapshot,
            pricingSnapshot = order.pricingSnapshot,
            items = order.items.map { OrderItemDetailResult.from(it) },
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            placedAt = order.placedAt,
            paidAt = order.paidAt,
            canceledAt = order.canceledAt,
        )
    }
}

data class OrderItemDetailResult(
    val itemId: Long,
    val skuId: Long,
    val productId: Long,
    val brandId: Long,
    val titleSnapshot: String,
    val optionSnapshot: String?,
    val qty: Int,
    val unitPrice: Long,
    val lineAmount: Long,
) {
    companion object {
        fun from(item: OrderItem): OrderItemDetailResult = OrderItemDetailResult(
            itemId = item.id!!,
            skuId = item.skuId,
            productId = item.productId,
            brandId = item.brandId,
            titleSnapshot = item.titleSnapshot,
            optionSnapshot = item.optionSnapshot,
            qty = item.qty,
            unitPrice = item.unitPrice.amount,
            lineAmount = item.lineAmount.amount,
        )
    }
}
