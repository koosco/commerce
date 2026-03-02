package com.koosco.inventoryservice.application.usecase

import com.koosco.inventoryservice.application.command.GetInventoryLogsCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.domain.enums.InventoryAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("GetInventoryLogsUseCase")
class GetInventoryLogsUseCaseTest {

    @Mock
    lateinit var inventoryLogPort: InventoryLogPort

    @InjectMocks
    lateinit var useCase: GetInventoryLogsUseCase

    @Nested
    @DisplayName("로그 조회")
    inner class GetLogs {

        @Test
        fun `SKU별 로그를 조회한다`() {
            val now = LocalDateTime.of(2025, 1, 1, 0, 0)
            val from = now.minusDays(7)
            val command = GetInventoryLogsCommand(skuId = "SKU-001", from = from, to = now)

            whenever(inventoryLogPort.findBySkuId("SKU-001", from, now))
                .thenReturn(
                    listOf(
                        InventoryLogPort.LogView(
                            id = 1L,
                            skuId = "SKU-001",
                            orderId = 100L,
                            action = InventoryAction.RESERVE,
                            quantity = 5,
                            createdAt = now.minusDays(1),
                        ),
                        InventoryLogPort.LogView(
                            id = 2L,
                            skuId = "SKU-001",
                            orderId = null,
                            action = InventoryAction.ADD,
                            quantity = 10,
                            createdAt = now,
                        ),
                    ),
                )

            val results = useCase.execute(command)

            assertThat(results).hasSize(2)
            assertThat(results[0].id).isEqualTo(1L)
            assertThat(results[0].action).isEqualTo(InventoryAction.RESERVE)
            assertThat(results[0].orderId).isEqualTo(100L)
            assertThat(results[1].id).isEqualTo(2L)
            assertThat(results[1].action).isEqualTo(InventoryAction.ADD)
            assertThat(results[1].orderId).isNull()
        }

        @Test
        fun `from과 to가 null이어도 조회한다`() {
            val command = GetInventoryLogsCommand(skuId = "SKU-001", from = null, to = null)

            whenever(inventoryLogPort.findBySkuId("SKU-001", null, null))
                .thenReturn(emptyList())

            val results = useCase.execute(command)

            assertThat(results).isEmpty()
        }
    }
}
