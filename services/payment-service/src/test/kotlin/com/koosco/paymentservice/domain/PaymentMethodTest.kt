package com.koosco.paymentservice.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("PaymentMethod 테스트")
class PaymentMethodTest {

    @Test
    fun `PaymentMethod 인스턴스를 생성할 수 있다`() {
        val paymentMethod = PaymentMethod()

        assertThat(paymentMethod).isNotNull()
    }
}
