package com.koosco.orderservice.domain.vo

import com.koosco.common.core.exception.BadRequestException
import com.koosco.orderservice.common.error.OrderErrorCode

data class OrderItemSpec(val skuId: String, val quantity: Int, val unitPrice: Money) {

    init {
        if (quantity <= 0) {
            throw BadRequestException(
                OrderErrorCode.INVALID_ORDER_STATUS,
                "수량은 1이상이어야 합니다.",
            )
        }
    }

    fun totalPrice(): Money = unitPrice * quantity

    fun discountAmount(orderTotalAmount: Money, orderDiscountAmount: Money): Money {
        if (orderDiscountAmount.isZero() || orderTotalAmount.isZero()) {
            return Money.ZERO
        }

        return Money(
            (totalPrice().amount * orderDiscountAmount.amount) / orderTotalAmount.amount,
        )
    }

    fun refundableAmount(orderTotalAmount: Money, orderDiscountAmount: Money): Money =
        totalPrice() - discountAmount(orderTotalAmount, orderDiscountAmount)
}
