package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.command.GetInventoriesCommand
import com.koosco.inventoryservice.application.command.GetInventoryCommand
import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("GetInventoryUseCase")
class GetInventoryUseCaseTest {

    @Mock
    lateinit var inventoryStockQuery: InventoryStockQueryPort

    @InjectMocks
    lateinit var useCase: GetInventoryUseCase

    @Nested
    @DisplayName("단일 조회")
    inner class GetSingle {

        @Test
        fun `SKU ID로 재고를 조회한다`() {
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 30, 70))

            val result = useCase.execute(GetInventoryCommand("SKU-001"))

            assertThat(result.skuId).isEqualTo("SKU-001")
            assertThat(result.totalStock).isEqualTo(100)
            assertThat(result.reservedStock).isEqualTo(30)
            assertThat(result.availableStock).isEqualTo(70)
        }
    }

    @Nested
    @DisplayName("대량 조회")
    inner class GetBulk {

        @Test
        fun `여러 SKU ID로 재고를 조회한다`() {
            val skuIds = listOf("SKU-001", "SKU-002")
            whenever(inventoryStockQuery.getStocks(skuIds)).thenReturn(
                listOf(
                    InventoryStockQueryPort.StockView("SKU-001", 100, 10, 90),
                    InventoryStockQueryPort.StockView("SKU-002", 200, 20, 180),
                ),
            )

            val results = useCase.execute(GetInventoriesCommand(skuIds))

            assertThat(results).hasSize(2)
            assertThat(results[0].skuId).isEqualTo("SKU-001")
            assertThat(results[1].skuId).isEqualTo("SKU-002")
        }

        @Test
        fun `빈 리스트 조회 시 빈 결과 반환`() {
            whenever(inventoryStockQuery.getStocks(emptyList())).thenReturn(emptyList())

            val results = useCase.execute(GetInventoriesCommand(emptyList()))

            assertThat(results).isEmpty()
        }
    }
}
