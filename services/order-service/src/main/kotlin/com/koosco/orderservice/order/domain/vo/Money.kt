package com.koosco.orderservice.order.domain.vo

@JvmInline
value class Money(val amount: Long) {

    init {
        require(amount >= 0) { "금액은 0 이상이어야 합니다. (amount=$amount)" }
    }

    companion object {
        val ZERO = Money(0)
    }

    fun isZero(): Boolean = amount == 0L

    private fun isPositive(): Boolean = amount > 0L

    operator fun plus(other: Money): Money = Money(this.amount + other.amount)

    operator fun minus(other: Money): Money {
        require(this.amount >= other.amount) {
            "금액이 부족합니다. (현재=${this.amount}, 차감=${other.amount})"
        }
        return Money(this.amount - other.amount)
    }

    operator fun times(quantity: Int): Money {
        require(quantity >= 0) { "수량은 0 이상이어야 합니다. (quantity=$quantity)" }
        return Money(this.amount * quantity)
    }
}
