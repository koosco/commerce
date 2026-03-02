package com.koosco.inventoryservice.domain.entity

import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import com.koosco.inventoryservice.domain.vo.Stock
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Inventory Entity")
class InventoryTest {

    private fun createInventory(total: Int = 100, reserved: Int = 0): Inventory =
        Inventory(skuId = "SKU-001", stock = Stock(total = total, reserved = reserved))

    @Nested
    @DisplayName("updateStock")
    inner class UpdateStock {

        @Test
        fun `재고를 업데이트한다`() {
            val inventory = createInventory(total = 100, reserved = 10)
            inventory.updateStock(200)

            assertThat(inventory.stock.total).isEqualTo(200)
            assertThat(inventory.stock.reserved).isEqualTo(10)
        }

        @Test
        fun `업데이트할 수량이 reserved보다 작으면 예외 발생`() {
            val inventory = createInventory(total = 100, reserved = 50)

            assertThatThrownBy { inventory.updateStock(30) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("increase")
    inner class Increase {

        @Test
        fun `재고를 증가시킨다`() {
            val inventory = createInventory(total = 100)
            inventory.increase(50)

            assertThat(inventory.stock.total).isEqualTo(150)
        }

        @Test
        fun `수량이 0이면 예외 발생`() {
            val inventory = createInventory()

            assertThatThrownBy { inventory.increase(0) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("decrease")
    inner class Decrease {

        @Test
        fun `재고를 감소시킨다`() {
            val inventory = createInventory(total = 100)
            inventory.decrease(30)

            assertThat(inventory.stock.total).isEqualTo(70)
        }

        @Test
        fun `감소 후 total이 reserved보다 작으면 예외 발생`() {
            val inventory = createInventory(total = 100, reserved = 80)

            assertThatThrownBy { inventory.decrease(30) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("reserve")
    inner class Reserve {

        @Test
        fun `재고를 예약한다`() {
            val inventory = createInventory(total = 100)
            inventory.reserve(30)

            assertThat(inventory.stock.reserved).isEqualTo(30)
            assertThat(inventory.stock.total).isEqualTo(100)
        }

        @Test
        fun `가용 재고보다 많이 예약하면 NotEnoughStockException 발생 - skuId 포함`() {
            val inventory = createInventory(total = 100, reserved = 90)

            assertThatThrownBy { inventory.reserve(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
                .satisfies({ ex ->
                    val nese = ex as NotEnoughStockException
                    assertThat(nese.skuId).isEqualTo("SKU-001")
                    assertThat(nese.requestedQuantity).isEqualTo(20)
                    assertThat(nese.availableQuantity).isEqualTo(10)
                })
        }
    }

    @Nested
    @DisplayName("confirm")
    inner class Confirm {

        @Test
        fun `예약을 확정한다`() {
            val inventory = createInventory(total = 100, reserved = 50)
            inventory.confirm(30)

            assertThat(inventory.stock.total).isEqualTo(70)
            assertThat(inventory.stock.reserved).isEqualTo(20)
        }

        @Test
        fun `예약 수량보다 많이 확정하면 예외 발생`() {
            val inventory = createInventory(total = 100, reserved = 10)

            assertThatThrownBy { inventory.confirm(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }

    @Nested
    @DisplayName("cancelReservation")
    inner class CancelReservation {

        @Test
        fun `예약을 취소한다`() {
            val inventory = createInventory(total = 100, reserved = 50)
            inventory.cancelReservation(30)

            assertThat(inventory.stock.total).isEqualTo(100)
            assertThat(inventory.stock.reserved).isEqualTo(20)
        }

        @Test
        fun `예약 수량보다 많이 취소하면 예외 발생`() {
            val inventory = createInventory(total = 100, reserved = 10)

            assertThatThrownBy { inventory.cancelReservation(20) }
                .isInstanceOf(NotEnoughStockException::class.java)
        }
    }
}
