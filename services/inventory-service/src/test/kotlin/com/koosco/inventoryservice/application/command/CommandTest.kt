package com.koosco.inventoryservice.application.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Command DTOs")
class CommandTest {

    @Nested
    @DisplayName("BulkAddStockCommand")
    inner class BulkAddStockCommandTest {

        @Test
        fun `커맨드를 생성한다`() {
            val items = listOf(
                BulkAddStockCommand.AddingStockInfo("SKU-001", 10),
                BulkAddStockCommand.AddingStockInfo("SKU-002", 20),
            )
            val command = BulkAddStockCommand(items = items, idempotencyKey = "key-1")

            assertThat(command.items).hasSize(2)
            assertThat(command.items[0].skuId).isEqualTo("SKU-001")
            assertThat(command.items[0].addingQuantity).isEqualTo(10)
            assertThat(command.idempotencyKey).isEqualTo("key-1")
        }

        @Test
        fun `멱등성 키 없이 커맨드를 생성한다`() {
            val command = BulkAddStockCommand(
                items = listOf(BulkAddStockCommand.AddingStockInfo("SKU-001", 10)),
            )

            assertThat(command.idempotencyKey).isNull()
        }
    }

    @Nested
    @DisplayName("BulkReduceStockCommand")
    inner class BulkReduceStockCommandTest {

        @Test
        fun `커맨드를 생성한다`() {
            val items = listOf(
                BulkReduceStockCommand.ReducingStockInfo("SKU-001", 5),
                BulkReduceStockCommand.ReducingStockInfo("SKU-002", 15),
            )
            val command = BulkReduceStockCommand(items = items, idempotencyKey = "key-2")

            assertThat(command.items).hasSize(2)
            assertThat(command.items[0].skuId).isEqualTo("SKU-001")
            assertThat(command.items[0].reducingQuantity).isEqualTo(5)
            assertThat(command.idempotencyKey).isEqualTo("key-2")
        }

        @Test
        fun `멱등성 키 없이 커맨드를 생성한다`() {
            val command = BulkReduceStockCommand(
                items = listOf(BulkReduceStockCommand.ReducingStockInfo("SKU-001", 5)),
            )

            assertThat(command.idempotencyKey).isNull()
        }
    }
}
