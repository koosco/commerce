package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.CancelStockCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockRestoredEvent
import com.koosco.inventoryservice.domain.enums.StockCancelReason
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("ReleaseStockUseCase")
class ReleaseStockUseCaseTest {

    @Mock
    lateinit var inventoryStockStore: InventoryStockStorePort

    @Mock
    lateinit var inventoryStockQuery: InventoryStockQueryPort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @Mock
    lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    lateinit var useCase: ReleaseStockUseCase

    private val context = MessageContext(correlationId = "corr-1", causationId = "cause-1")

    private fun command(orderId: Long = 1L, skuId: String = "SKU-001", quantity: Int = 5) = CancelStockCommand(
        orderId = orderId,
        items = listOf(CancelStockCommand.CancelledSku(skuId, quantity)),
        reason = StockCancelReason.USER_CANCELLED,
    )

    @Nested
    @DisplayName("성공")
    inner class Success {

        @Test
        fun `취소 성공 시 로그 기록`() {
            val cmd = command()
            // 취소 전 가용 재고가 10이라 복구 이벤트 불필요
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 90, 10))

            useCase.execute(cmd, context)

            verify(inventoryStockStore).cancel(eq(1L), any())
            verify(inventoryLogPort).logBatch(any())
        }

        @Test
        fun `취소 전 가용 재고가 0이고 취소 후 복구되면 StockRestored 이벤트 발행`() {
            val cmd = command()
            // 첫 호출: 취소 전 가용 재고 0, 두 번째 호출: 취소 후 가용 재고 5
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 100, 0))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 95, 5))

            useCase.execute(cmd, context)

            verify(integrationEventProducer).publish(any<StockRestoredEvent>())
        }

        @Test
        fun `취소 전 가용 재고가 0이지만 취소 후에도 0이면 StockRestored 발행하지 않음`() {
            val cmd = command()
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 100, 0))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 100, 0))

            useCase.execute(cmd, context)

            verify(integrationEventProducer, never()).publish(any<StockRestoredEvent>())
        }

        @Test
        fun `취소 전 가용 재고 조회 실패 시 StockRestored 발행하지 않음`() {
            val cmd = command()
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenThrow(RuntimeException("query failed"))

            useCase.execute(cmd, context)

            verify(inventoryStockStore).cancel(eq(1L), any())
            verify(integrationEventProducer, never()).publish(any<StockRestoredEvent>())
        }

        @Test
        fun `취소 후 재고 조회 실패 시에도 취소는 성공`() {
            val cmd = command()
            // 첫 호출: 취소 전 가용 재고 0
            // 두 번째 호출: 취소 후 조회 실패
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 100, 0))
                .thenThrow(RuntimeException("query failed after cancel"))

            useCase.execute(cmd, context)

            verify(inventoryStockStore).cancel(eq(1L), any())
            verify(integrationEventProducer, never()).publish(any<StockRestoredEvent>())
        }
    }
}
