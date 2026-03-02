package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.ConfirmStockCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockDepletedEvent
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("ConfirmStockUseCase")
class ConfirmStockUseCaseTest {

    @Mock
    lateinit var inventoryStockStore: InventoryStockStorePort

    @Mock
    lateinit var inventoryStockQuery: InventoryStockQueryPort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @Mock
    lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    lateinit var useCase: ConfirmStockUseCase

    private val context = MessageContext(correlationId = "corr-1", causationId = "cause-1")

    private fun command(orderId: Long = 1L, skuId: String = "SKU-001", quantity: Int = 5) = ConfirmStockCommand(
        orderId = orderId,
        items = listOf(ConfirmStockCommand.ConfirmedSku(skuId, quantity)),
    )

    @Nested
    @DisplayName("성공")
    inner class Success {

        @Test
        fun `확정 성공 시 로그 기록 및 이벤트 발행 - 재고 남아있음`() {
            val cmd = command()
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 100, 0, 100))

            useCase.execute(cmd, context)

            verify(inventoryStockStore).confirm(eq(1L), any())
            verify(inventoryLogPort).logBatch(any())
            verify(integrationEventProducer).publish(any<StockConfirmedEvent>())
            verify(integrationEventProducer, never()).publish(any<StockDepletedEvent>())
        }

        @Test
        fun `확정 성공 시 가용 재고가 0이면 StockDepleted 이벤트 발행`() {
            val cmd = command()
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenReturn(InventoryStockQueryPort.StockView("SKU-001", 5, 5, 0))

            useCase.execute(cmd, context)

            verify(integrationEventProducer).publish(any<StockConfirmedEvent>())
            verify(integrationEventProducer).publish(any<StockDepletedEvent>())
        }

        @Test
        fun `재고 소진 확인 중 예외가 발생해도 확정은 성공`() {
            val cmd = command()
            whenever(inventoryStockQuery.getStock("SKU-001"))
                .thenThrow(RuntimeException("query failed"))

            useCase.execute(cmd, context)

            verify(inventoryStockStore).confirm(eq(1L), any())
            verify(integrationEventProducer).publish(any<StockConfirmedEvent>())
        }
    }

    @Nested
    @DisplayName("실패")
    inner class Failure {

        @Test
        fun `SKU를 찾을 수 없으면 실패 이벤트 발행 후 예외 재발생`() {
            val cmd = command()
            whenever(inventoryStockStore.confirm(any(), any())).thenThrow(NotFoundException())

            assertThatThrownBy { useCase.execute(cmd, context) }
                .isInstanceOf(NotFoundException::class.java)

            verify(integrationEventProducer).publish(any<StockConfirmFailedEvent>())
            verify(inventoryLogPort, never()).logBatch(any())
        }

        @Test
        fun `예약 재고 부족 시 실패 이벤트 발행 후 예외 재발생`() {
            val cmd = command()
            whenever(inventoryStockStore.confirm(any(), any()))
                .thenThrow(NotEnoughStockException("Not enough reserved"))

            assertThatThrownBy { useCase.execute(cmd, context) }
                .isInstanceOf(NotEnoughStockException::class.java)

            verify(integrationEventProducer).publish(any<StockConfirmFailedEvent>())
        }
    }
}
