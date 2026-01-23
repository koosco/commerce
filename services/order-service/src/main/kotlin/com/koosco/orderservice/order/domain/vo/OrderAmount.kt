package com.koosco.orderservice.order.domain.vo

import kotlin.collections.map

class OrderAmount private constructor(val total: Money, val discount: Money, val payable: Money) {

    companion object {
        fun from(itemSpecs: List<OrderItemSpec>, discount: Money): OrderAmount {
            val total = itemSpecs
                .map { it.totalPrice() }
                .fold(Money.ZERO) { acc, price -> acc + price }

            require(discount.amount <= total.amount) {
                "할인 금액은 주문 금액을 초과할 수 없습니다. (total=${total.amount}, discount=${discount.amount})"
            }

            val payable = total - discount

            return OrderAmount(
                total = total,
                discount = discount,
                payable = payable,
            )
        }
    }
}
