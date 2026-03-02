package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.port.InventorySeedPort
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
@DisplayName("InventorySeedUseCase")
class InventorySeedUseCaseTest {

    @Mock
    lateinit var inventorySeedPort: InventorySeedPort

    @InjectMocks
    lateinit var useCase: InventorySeedUseCase

    @Nested
    @DisplayName("init")
    inner class Init {

        @Test
        fun `SKU-001과 SKU-002를 10000개로 초기화한다`() {
            useCase.init()

            verify(inventorySeedPort).initStock("SKU-001", 10_000)
            verify(inventorySeedPort).initStock("SKU-002", 10_000)
        }
    }

    @Nested
    @DisplayName("clear")
    inner class Clear {

        @Test
        fun `clear를 2번 호출한다`() {
            useCase.clear()

            verify(inventorySeedPort, times(2)).clear()
        }
    }
}
