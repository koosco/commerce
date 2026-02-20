package com.koosco.orderservice.api

import com.koosco.orderservice.application.result.CreateOrderResult
import com.koosco.orderservice.application.result.OrderDetailResult
import com.koosco.orderservice.application.result.OrderItemDetailResult
import com.koosco.orderservice.application.result.OrderListResult
import com.koosco.orderservice.domain.enums.OrderStatus
import java.time.LocalDateTime

data class OrderResponse(
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
        fun from(order: OrderListResult): OrderResponse = OrderResponse(
            orderId = order.orderId,
            orderNo = order.orderNo,
            userId = order.userId,
            status = order.status,
            subtotalAmount = order.subtotalAmount,
            discountAmount = order.discountAmount,
            shippingFee = order.shippingFee,
            totalAmount = order.totalAmount,
            currency = order.currency,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}

data class OrderItemResponse(
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
        fun from(item: OrderItemDetailResult): OrderItemResponse = OrderItemResponse(
            itemId = item.itemId,
            skuId = item.skuId,
            productId = item.productId,
            brandId = item.brandId,
            titleSnapshot = item.titleSnapshot,
            optionSnapshot = item.optionSnapshot,
            qty = item.qty,
            unitPrice = item.unitPrice,
            lineAmount = item.lineAmount,
        )
    }
}

data class OrderDetailResponse(
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
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val placedAt: LocalDateTime?,
    val paidAt: LocalDateTime?,
    val canceledAt: LocalDateTime?,
) {
    companion object {
        fun from(result: OrderDetailResult): OrderDetailResponse = OrderDetailResponse(
            orderId = result.orderId,
            orderNo = result.orderNo,
            userId = result.userId,
            status = result.status,
            subtotalAmount = result.subtotalAmount,
            discountAmount = result.discountAmount,
            shippingFee = result.shippingFee,
            totalAmount = result.totalAmount,
            currency = result.currency,
            shippingAddressSnapshot = result.shippingAddressSnapshot,
            items = result.items.map { OrderItemResponse.from(it) },
            createdAt = result.createdAt,
            updatedAt = result.updatedAt,
            placedAt = result.placedAt,
            paidAt = result.paidAt,
            canceledAt = result.canceledAt,
        )
    }
}

data class CreateOrderResponse(
    val orderId: Long,
    val orderNo: String,
    val status: OrderStatus,
    val totalAmount: Long,
) {
    companion object {
        fun from(result: CreateOrderResult): CreateOrderResponse = CreateOrderResponse(
            orderId = result.orderId,
            orderNo = result.orderNo,
            status = result.status,
            totalAmount = result.totalAmount,
        )
    }
}
