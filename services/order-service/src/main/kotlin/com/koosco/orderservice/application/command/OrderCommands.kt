package com.koosco.orderservice.application.command

import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.vo.Money

/**
 * 주문 생성 command
 */
data class CreateOrderCommand(
    val userId: Long,
    val idempotencyKey: String?,
    val items: List<OrderItemCommand>,
    val discountAmount: Money = Money(0L),
    val shippingFee: Money = Money(0L),
    val shippingAddress: ShippingAddressCommand,
) {
    data class OrderItemCommand(
        val skuId: Long,
        val productId: Long,
        val brandId: Long,
        val titleSnapshot: String,
        val optionSnapshot: String?,
        val quantity: Int,
        val unitPrice: Money,
    )

    data class ShippingAddressCommand(
        val recipient: String,
        val phone: String,
        val zipCode: String,
        val address: String,
        val addressDetail: String,
    )
}

/**
 * 주문 확정 command
 */
data class MarkOrderPaidCommand(val orderId: Long, val paidAmount: Long)

/**
 * 주문 취소 command
 */
data class CancelOrderCommand(val orderId: Long, val reason: OrderCancelReason)

/**
 * 주문 실패 처리 command (재고 예약 실패 등 초기 단계 실패)
 */
data class MarkOrderFailedCommand(val orderId: Long, val reason: String)
