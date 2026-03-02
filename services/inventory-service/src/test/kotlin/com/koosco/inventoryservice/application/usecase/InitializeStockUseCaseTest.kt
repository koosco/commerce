package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.command.InitStockCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.domain.enums.InventoryAction
import com.koosco.inventoryservice.domain.exception.InventoryAlreadyInitialized
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
@DisplayName("InitializeStockUseCase")
class InitializeStockUseCaseTest {

    @Mock
    lateinit var inventoryStockStore: InventoryStockStorePort

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @InjectMocks
    lateinit var useCase: InitializeStockUseCase

    @Nested
    @DisplayName("성공")
    inner class Success {

        @Test
        fun `재고 초기화 성공 시 로그 기록`() {
            val cmd = InitStockCommand(skuId = "SKU-001", initialQuantity = 100)

            useCase.execute(cmd)

            verify(inventoryStockStore).initialize("SKU-001", 100)
            verify(inventoryLogPort).log(
                skuId = "SKU-001",
                orderId = null,
                action = InventoryAction.INITIALIZE,
                quantity = 100,
            )
        }
    }

    @Nested
    @DisplayName("실패")
    inner class Failure {

        @Test
        fun `이미 초기화된 재고이면 예외 재발생`() {
            val cmd = InitStockCommand(skuId = "SKU-001", initialQuantity = 100)
            whenever(inventoryStockStore.initialize(any(), any()))
                .thenThrow(InventoryAlreadyInitialized())

            assertThatThrownBy { useCase.execute(cmd) }
                .isInstanceOf(InventoryAlreadyInitialized::class.java)

            verify(inventoryLogPort, never()).log(any(), any(), any(), any())
        }
    }
}
