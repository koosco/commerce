package com.koosco.inventoryservice.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.inventoryservice.api.request.GetInventoriesRequest
import com.koosco.inventoryservice.application.result.GetInventoryResult
import com.koosco.inventoryservice.application.usecase.GetInventoryUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(QueryInventoryController::class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("QueryInventoryController")
class QueryInventoryControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var getInventoryUseCase: GetInventoryUseCase

    @Nested
    @DisplayName("GET /api/inventories/{skuId}")
    inner class GetInventoryBySkuId {

        @Test
        fun `단일 재고 조회 성공`() {
            whenever(
                getInventoryUseCase.execute(any<com.koosco.inventoryservice.application.command.GetInventoryCommand>()),
            )
                .thenReturn(
                    GetInventoryResult(
                        skuId = "SKU-001",
                        totalStock = 100,
                        reservedStock = 30,
                        availableStock = 70,
                    ),
                )

            mockMvc.get("/api/inventories/SKU-001")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.skuId") { value("SKU-001") }
                    jsonPath("$.data.totalStock") { value(100) }
                    jsonPath("$.data.reservedStock") { value(30) }
                    jsonPath("$.data.availableStock") { value(70) }
                }
        }
    }

    @Nested
    @DisplayName("POST /api/inventories/bulk")
    inner class GetInventoryBySkuIds {

        @Test
        fun `대량 재고 조회 성공`() {
            whenever(
                getInventoryUseCase.execute(
                    any<com.koosco.inventoryservice.application.command.GetInventoriesCommand>(),
                ),
            )
                .thenReturn(
                    listOf(
                        GetInventoryResult("SKU-001", 100, 10, 90),
                        GetInventoryResult("SKU-002", 200, 20, 180),
                    ),
                )

            val request = GetInventoriesRequest(skuIds = listOf("SKU-001", "SKU-002"))

            mockMvc.post("/api/inventories/bulk") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.inventories.length()") { value(2) }
                jsonPath("$.data.inventories[0].skuId") { value("SKU-001") }
                jsonPath("$.data.inventories[1].skuId") { value("SKU-002") }
            }
        }
    }
}
