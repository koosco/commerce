package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.command.BulkAddStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("AddStockUseCase")
class AddStockUseCaseTest {

    @Mock
    lateinit var inventoryStockStore: InventoryStockStorePort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @Mock
    lateinit var apiIdempotencyRepository: InventoryApiIdempotencyRepository

    @InjectMocks
    lateinit var useCase: AddStockUseCase

    private fun command(skuId: String = "SKU-001", quantity: Int = 10, idempotencyKey: String? = null) =
        BulkAddStockCommand(
            items = listOf(BulkAddStockCommand.AddingStockInfo(skuId, quantity)),
            idempotencyKey = idempotencyKey,
        )

    @Nested
    @DisplayName("멱등성 키 없이")
    inner class WithoutIdempotencyKey {

        @Test
        fun `재고 추가 성공`() {
            useCase.execute(command())

            verify(inventoryStockStore).add(any())
            verify(inventoryLogPort).logBatch(any())
            verify(apiIdempotencyRepository, never()).existsByIdempotencyKeyAndOperationType(any(), any())
            verify(apiIdempotencyRepository, never()).save(any())
        }
    }

    @Nested
    @DisplayName("멱등성 키 포함")
    inner class WithIdempotencyKey {

        @Test
        fun `새로운 키이면 재고 추가 후 멱등성 저장`() {
            whenever(
                apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    "key-1",
                    InventoryApiIdempotency.ADD_STOCK,
                ),
            ).thenReturn(false)

            useCase.execute(command(idempotencyKey = "key-1"))

            verify(inventoryStockStore).add(any())
            verify(inventoryLogPort).logBatch(any())
            verify(apiIdempotencyRepository).save(any())
        }

        @Test
        fun `이미 처리된 키이면 아무 것도 하지 않는다`() {
            whenever(
                apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    "key-1",
                    InventoryApiIdempotency.ADD_STOCK,
                ),
            ).thenReturn(true)

            useCase.execute(command(idempotencyKey = "key-1"))

            verify(inventoryStockStore, never()).add(any())
            verify(inventoryLogPort, never()).logBatch(any())
        }
    }
}
