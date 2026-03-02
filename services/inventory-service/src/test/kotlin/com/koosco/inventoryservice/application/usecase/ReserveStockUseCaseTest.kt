package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.ReserveStockCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservedEvent
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
@DisplayName("ReserveStockUseCase")
class ReserveStockUseCaseTest {

    @Mock
    lateinit var inventoryStockStore: InventoryStockStorePort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @Mock
    lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    lateinit var useCase: ReserveStockUseCase

    private val context = MessageContext(correlationId = "corr-1", causationId = "cause-1")

    private fun command(orderId: Long = 1L, skuId: String = "SKU-001", quantity: Int = 5) = ReserveStockCommand(
        orderId = orderId,
        items = listOf(ReserveStockCommand.ReservedSku(skuId, quantity)),
    )

    @Nested
    @DisplayName("성공")
    inner class Success {

        @Test
        fun `재고 예약 성공 시 로그 기록 및 이벤트 발행`() {
            val cmd = command()

            useCase.execute(cmd, context)

            verify(inventoryStockStore).reserve(eq(1L), any())
            verify(inventoryLogPort).logBatch(any())
            verify(integrationEventProducer).publish(any<StockReservedEvent>())
        }
    }

    @Nested
    @DisplayName("실패")
    inner class Failure {

        @Test
        fun `SKU를 찾을 수 없으면 실패 이벤트 발행 후 예외 재발생`() {
            val cmd = command()
            whenever(inventoryStockStore.reserve(any(), any())).thenThrow(NotFoundException())

            assertThatThrownBy { useCase.execute(cmd, context) }
                .isInstanceOf(NotFoundException::class.java)

            verify(integrationEventProducer).publish(any<StockReservationFailedEvent>())
            verify(inventoryLogPort, never()).logBatch(any())
        }

        @Test
        fun `재고 부족 시 실패 이벤트 발행 후 예외 재발생`() {
            val cmd = command()
            whenever(inventoryStockStore.reserve(any(), any()))
                .thenThrow(NotEnoughStockException("Not enough"))

            assertThatThrownBy { useCase.execute(cmd, context) }
                .isInstanceOf(NotEnoughStockException::class.java)

            verify(integrationEventProducer).publish(any<StockReservationFailedEvent>())
            verify(inventoryLogPort, never()).logBatch(any())
        }
    }
}
