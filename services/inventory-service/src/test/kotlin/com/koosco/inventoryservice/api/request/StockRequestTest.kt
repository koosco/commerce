package com.koosco.inventoryservice.api.request

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Stock Request DTOs")
class StockRequestTest {

    @Nested
    @DisplayName("BulkAddStockRequest")
    inner class BulkAddStockRequestTest {

        @Test
        fun `요청을 생성한다`() {
            val request = BulkAddStockRequest(
                items = listOf(
                    BulkAddStockRequest.AddingStockInfo("SKU-001", 10),
                ),
                idempotencyKey = "key-1",
            )

            assertThat(request.items).hasSize(1)
            assertThat(request.items[0].skuId).isEqualTo("SKU-001")
            assertThat(request.items[0].quantity).isEqualTo(10)
            assertThat(request.idempotencyKey).isEqualTo("key-1")
        }
    }

    @Nested
    @DisplayName("BulkReduceStockRequest")
    inner class BulkReduceStockRequestTest {

        @Test
        fun `요청을 생성한다`() {
            val request = BulkReduceStockRequest(
                items = listOf(
                    BulkReduceStockRequest.ReducingStockInfo("SKU-001", 5),
                    BulkReduceStockRequest.ReducingStockInfo("SKU-002", 15),
                ),
                idempotencyKey = "key-2",
            )

            assertThat(request.items).hasSize(2)
            assertThat(request.items[0].skuId).isEqualTo("SKU-001")
            assertThat(request.items[0].quantity).isEqualTo(5)
            assertThat(request.idempotencyKey).isEqualTo("key-2")
        }

        @Test
        fun `멱등성 키 없이 요청을 생성한다`() {
            val request = BulkReduceStockRequest(
                items = listOf(BulkReduceStockRequest.ReducingStockInfo("SKU-001", 5)),
            )

            assertThat(request.idempotencyKey).isNull()
        }
    }

    @Nested
    @DisplayName("GetInventoriesRequest")
    inner class GetInventoriesRequestTest {

        @Test
        fun `조회 요청을 생성한다`() {
            val request = GetInventoriesRequest(skuIds = listOf("SKU-001", "SKU-002"))

            assertThat(request.skuIds).hasSize(2)
        }
    }
}
