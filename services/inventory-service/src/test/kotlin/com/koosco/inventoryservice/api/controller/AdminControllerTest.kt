package com.koosco.inventoryservice.api.controller

import com.koosco.inventoryservice.application.result.GetInventoryLogsResult
import com.koosco.inventoryservice.application.usecase.GetInventoryLogsUseCase
import com.koosco.inventoryservice.domain.enums.InventoryAction
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@WebMvcTest(AdminController::class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController")
class AdminControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var getInventoryLogsUseCase: GetInventoryLogsUseCase

    @Nested
    @DisplayName("GET /api/inventories/logs")
    inner class GetInventoryChangeLogs {

        @Test
        fun `재고 변경 로그를 조회한다`() {
            val now = LocalDateTime.of(2025, 1, 15, 10, 0)
            whenever(getInventoryLogsUseCase.execute(any()))
                .thenReturn(
                    listOf(
                        GetInventoryLogsResult(
                            id = 1L,
                            skuId = "SKU-001",
                            orderId = 100L,
                            action = InventoryAction.RESERVE,
                            quantity = 5,
                            createdAt = now,
                        ),
                    ),
                )

            mockMvc.get("/api/inventories/logs") {
                param("skuId", "SKU-001")
                param("from", "2025-01-01T00:00:00")
                param("to", "2025-01-31T23:59:59")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.logs.length()") { value(1) }
                jsonPath("$.data.logs[0].skuId") { value("SKU-001") }
                jsonPath("$.data.logs[0].action") { value("RESERVE") }
                jsonPath("$.data.logs[0].quantity") { value(5) }
            }
        }

        @Test
        fun `from과 to 없이 재고 로그를 조회한다`() {
            whenever(getInventoryLogsUseCase.execute(any()))
                .thenReturn(emptyList())

            mockMvc.get("/api/inventories/logs") {
                param("skuId", "SKU-001")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.logs.length()") { value(0) }
            }
        }
    }
}
