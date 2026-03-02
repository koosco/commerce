package com.koosco.inventoryservice.domain.vo

import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Stock Value Object")
class StockTest {

    @Nested
    @DisplayName("생성")
    inner class Creation {

        @Test
        fun `정상적인 Stock 생성`() {
            val stock = Stock(total = 100, reserved = 30)

            assertThat(stock.total).isEqualTo(100)
            assertThat(stock.reserved).isEqualTo(30)
            assertThat(stock.available).isEqualTo(70)
        }

        @Test
        fun `reserved 기본값은 0`() {
            val stock = Stock(total = 100)

            assertThat(stock.reserved).isEqualTo(0)
            assertThat(stock.available).isEqualTo(100)
        }

        @Test
        fun `total이 음수이면 예외 발생`() {
            assertThatThrownBy { Stock(total = -1, reserved = 0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `reserved가 음수이면 예외 발생`() {
            assertThatThrownBy { Stock(total = 10, reserved = -1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `reserved가 total보다 크면 예외 발생`() {
            assertThatThrownBy { Stock(total = 5, reserved = 10) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("increase")
    inner class Increase {

        @Test
        fun `재고를 증가시킨다`() {
            val stock = Stock(total = 100, reserved = 10)
            val result = stock.increase(50)

            assertThat(result.total).isEqualTo(150)
            assertThat(result.reserved).isEqualTo(10)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.increase(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.increase(-1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("decrease")
    inner class Decrease {

        @Test
        fun `재고를 감소시킨다`() {
            val stock = Stock(total = 100, reserved = 10)
            val result = stock.decrease(50)

            assertThat(result.total).isEqualTo(50)
            assertThat(result.reserved).isEqualTo(10)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.decrease(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.decrease(-1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `감소 후 total이 reserved보다 작아지면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 80)

            assertThatThrownBy { stock.decrease(30) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("reserve")
    inner class Reserve {

        @Test
        fun `재고를 예약한다`() {
            val stock = Stock(total = 100, reserved = 10)
            val result = stock.reserve(50)

            assertThat(result.total).isEqualTo(100)
            assertThat(result.reserved).isEqualTo(60)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.reserve(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `가용 재고보다 많이 예약하면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 90)

            assertThatThrownBy { stock.reserve(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외 발생`() {
            val stock = Stock(total = 100)

            assertThatThrownBy { stock.reserve(-1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("confirm")
    inner class Confirm {

        @Test
        fun `예약을 확정한다`() {
            val stock = Stock(total = 100, reserved = 50)
            val result = stock.confirm(30)

            assertThat(result.total).isEqualTo(70)
            assertThat(result.reserved).isEqualTo(20)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 50)

            assertThatThrownBy { stock.confirm(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 50)

            assertThatThrownBy { stock.confirm(-1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `예약 수량보다 많이 확정하면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 10)

            assertThatThrownBy { stock.confirm(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("cancelReservation")
    inner class CancelReservation {

        @Test
        fun `예약을 취소한다`() {
            val stock = Stock(total = 100, reserved = 50)
            val result = stock.cancelReservation(30)

            assertThat(result.total).isEqualTo(100)
            assertThat(result.reserved).isEqualTo(20)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 50)

            assertThatThrownBy { stock.cancelReservation(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 50)

            assertThatThrownBy { stock.cancelReservation(-1) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }

        @Test
        fun `예약 수량보다 많이 취소하면 예외 발생`() {
            val stock = Stock(total = 100, reserved = 10)

            assertThatThrownBy { stock.cancelReservation(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }
}
