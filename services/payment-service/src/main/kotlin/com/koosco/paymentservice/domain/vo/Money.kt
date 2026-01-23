package com.koosco.paymentservice.domain.vo

import com.koosco.common.core.exception.BadRequestException
import com.koosco.paymentservice.common.PaymentErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Money(
    @Column(name = "amount", nullable = false)
    val value: Long,
) : Comparable<Money> {
    init {
        if (value < 0) {
            throw BadRequestException(
                errorCode = PaymentErrorCode.INVALID_PAYMENT_AMOUNT,
                message = "금액이 0원 이하가 될 수 없습니다",
            )
        }
    }

    override operator fun compareTo(other: Money): Int = this.value.compareTo(other.value)

    operator fun plus(other: Money): Money = Money(this.value + other.value)

    operator fun minus(other: Money): Money = Money(this.value - other.value)
}
