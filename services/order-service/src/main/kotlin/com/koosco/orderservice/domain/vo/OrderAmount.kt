package com.koosco.orderservice.domain.vo

class OrderAmount private constructor(
    val subtotal: Money,
    val discount: Money,
    val shippingFee: Money,
    val total: Money,
) {

    companion object {
        fun from(itemSpecs: List<OrderItemSpec>, discount: Money, shippingFee: Money = Money.ZERO): OrderAmount {
            val subtotal = itemSpecs
                .map { it.totalPrice() }
                .fold(Money.ZERO) { acc, price -> acc + price }

            require(discount.amount <= subtotal.amount) {
                "할인 금액은 주문 금액을 초과할 수 없습니다. (subtotal=${subtotal.amount}, discount=${discount.amount})"
            }

            val total = subtotal - discount + shippingFee

            return OrderAmount(
                subtotal = subtotal,
                discount = discount,
                shippingFee = shippingFee,
                total = total,
            )
        }
    }
}
