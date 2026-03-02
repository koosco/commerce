package com.koosco.paymentservice.domain

import com.koosco.common.core.exception.BadRequestException
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Money 값 객체 테스트")
class MoneyTest {

    @Nested
    @DisplayName("Money 생성")
    inner class Create {

        @Test
        fun `0원으로 생성할 수 있다`() {
            val money = Money(0)

            assertThat(money.value).isEqualTo(0)
        }

        @Test
        fun `양수 금액으로 생성할 수 있다`() {
            val money = Money(10000)

            assertThat(money.value).isEqualTo(10000)
        }

        @Test
        fun `음수 금액으로 생성하면 예외가 발생한다`() {
            assertThatThrownBy { Money(-1) }
                .isInstanceOf(BadRequestException::class.java)
        }
    }

    @Nested
    @DisplayName("Money 연산")
    inner class Operations {

        @Test
        fun `두 Money를 더할 수 있다`() {
            val result = Money(3000) + Money(5000)

            assertThat(result).isEqualTo(Money(8000))
        }

        @Test
        fun `두 Money를 뺄 수 있다`() {
            val result = Money(10000) - Money(3000)

            assertThat(result).isEqualTo(Money(7000))
        }

        @Test
        fun `뺄셈 결과가 음수이면 예외가 발생한다`() {
            assertThatThrownBy { Money(3000) - Money(5000) }
                .isInstanceOf(BadRequestException::class.java)
        }
    }

    @Nested
    @DisplayName("Money 비교")
    inner class Compare {

        @Test
        fun `같은 금액은 동일하다`() {
            assertThat(Money(10000)).isEqualTo(Money(10000))
        }

        @Test
        fun `다른 금액은 동일하지 않다`() {
            assertThat(Money(10000)).isNotEqualTo(Money(5000))
        }

        @Test
        fun `큰 금액이 compareTo에서 양수를 반환한다`() {
            assertThat(Money(10000).compareTo(Money(5000))).isGreaterThan(0)
        }

        @Test
        fun `작은 금액이 compareTo에서 음수를 반환한다`() {
            assertThat(Money(5000).compareTo(Money(10000))).isLessThan(0)
        }

        @Test
        fun `같은 금액이 compareTo에서 0을 반환한다`() {
            assertThat(Money(10000).compareTo(Money(10000))).isEqualTo(0)
        }

        @Test
        fun `Money는 비교 연산자를 사용할 수 있다`() {
            assertThat(Money(10000) > Money(5000)).isTrue()
            assertThat(Money(5000) < Money(10000)).isTrue()
            assertThat(Money(10000) >= Money(10000)).isTrue()
            assertThat(Money(10000) <= Money(10000)).isTrue()
        }
    }
}
