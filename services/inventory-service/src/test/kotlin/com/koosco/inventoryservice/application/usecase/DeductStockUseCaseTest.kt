package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.command.DeductStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryRepositoryPort
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("DeductStockUseCase")
class DeductStockUseCaseTest {

    @Mock
    lateinit var inventoryRepositoryPort: InventoryRepositoryPort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @Mock
    lateinit var apiIdempotencyRepository: InventoryApiIdempotencyRepository

    @InjectMocks
    lateinit var useCase: DeductStockUseCase

    private fun command(
        orderId: Long = 1L,
        skuId: String = "SKU-001",
        quantity: Int = 3,
        idempotencyKey: String? = null,
    ) = DeductStockCommand(
        orderId = orderId,
        items = listOf(DeductStockCommand.DeductItem(skuId, quantity)),
        idempotencyKey = idempotencyKey,
    )

    @Nested
    @DisplayName("단일 요청")
    inner class SingleRequest {

        @Test
        fun `재고 차감 성공`() {
            whenever(inventoryRepositoryPort.deductQuantity("SKU-001", 3)).thenReturn(1)

            useCase.execute(command())

            verify(inventoryRepositoryPort).deductQuantity("SKU-001", 3)
            verify(inventoryLogPort).logBatch(any())
        }

        @Test
        fun `재고 부족 시 NotEnoughStockException 발생`() {
            whenever(inventoryRepositoryPort.deductQuantity("SKU-001", 3)).thenReturn(0)

            assertThrows<NotEnoughStockException> {
                useCase.execute(command())
            }

            verify(inventoryRepositoryPort).deductQuantity("SKU-001", 3)
            verify(inventoryLogPort, never()).logBatch(any())
        }
    }

    @Nested
    @DisplayName("여러 SKU 차감")
    inner class MultipleItems {

        @Test
        fun `모든 SKU 차감 성공`() {
            val cmd = DeductStockCommand(
                orderId = 1L,
                items = listOf(
                    DeductStockCommand.DeductItem("SKU-001", 2),
                    DeductStockCommand.DeductItem("SKU-002", 5),
                ),
            )

            whenever(inventoryRepositoryPort.deductQuantity("SKU-001", 2)).thenReturn(1)
            whenever(inventoryRepositoryPort.deductQuantity("SKU-002", 5)).thenReturn(1)

            useCase.execute(cmd)

            verify(inventoryRepositoryPort).deductQuantity("SKU-001", 2)
            verify(inventoryRepositoryPort).deductQuantity("SKU-002", 5)
            verify(inventoryLogPort).logBatch(argThat { size == 2 })
        }

        @Test
        fun `두 번째 SKU 재고 부족 시 예외 발생`() {
            val cmd = DeductStockCommand(
                orderId = 1L,
                items = listOf(
                    DeductStockCommand.DeductItem("SKU-001", 2),
                    DeductStockCommand.DeductItem("SKU-002", 5),
                ),
            )

            whenever(inventoryRepositoryPort.deductQuantity("SKU-001", 2)).thenReturn(1)
            whenever(inventoryRepositoryPort.deductQuantity("SKU-002", 5)).thenReturn(0)

            assertThrows<NotEnoughStockException> {
                useCase.execute(cmd)
            }
        }
    }

    @Nested
    @DisplayName("멱등성 키 처리")
    inner class WithIdempotencyKey {

        @Test
        fun `새로운 키이면 차감 후 멱등성 저장`() {
            whenever(
                apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    "key-1",
                    InventoryApiIdempotency.DEDUCT_STOCK,
                ),
            ).thenReturn(false)
            whenever(inventoryRepositoryPort.deductQuantity("SKU-001", 3)).thenReturn(1)

            useCase.execute(command(idempotencyKey = "key-1"))

            verify(inventoryRepositoryPort).deductQuantity("SKU-001", 3)
            verify(inventoryLogPort).logBatch(any())
            verify(apiIdempotencyRepository).save(any())
        }

        @Test
        fun `이미 처리된 키이면 아무 것도 하지 않는다`() {
            whenever(
                apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    "key-1",
                    InventoryApiIdempotency.DEDUCT_STOCK,
                ),
            ).thenReturn(true)

            useCase.execute(command(idempotencyKey = "key-1"))

            verify(inventoryRepositoryPort, never()).deductQuantity(any(), any())
            verify(inventoryLogPort, never()).logBatch(any())
        }
    }
}
