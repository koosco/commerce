package com.koosco.inventoryservice.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.inventoryservice.api.request.BulkAddStockRequest
import com.koosco.inventoryservice.api.request.BulkReduceStockRequest
import com.koosco.inventoryservice.application.usecase.AddStockUseCase
import com.koosco.inventoryservice.application.usecase.ReduceStockUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(InventoryStockController::class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("InventoryStockController")
class InventoryStockControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var addStockUseCase: AddStockUseCase

    @MockitoBean
    lateinit var reduceStockUseCase: ReduceStockUseCase

    @Nested
    @DisplayName("POST /api/inventories/increase")
    inner class AddBulkInventories {

        @Test
        fun `대량 재고 추가 성공`() {
            val request = BulkAddStockRequest(
                items = listOf(
                    BulkAddStockRequest.AddingStockInfo("SKU-001", 10),
                    BulkAddStockRequest.AddingStockInfo("SKU-002", 20),
                ),
                idempotencyKey = "key-1",
            )

            mockMvc.post("/api/inventories/increase") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }

            verify(addStockUseCase).execute(any())
        }

        @Test
        fun `멱등성 키 없이 대량 재고 추가 성공`() {
            val request = BulkAddStockRequest(
                items = listOf(BulkAddStockRequest.AddingStockInfo("SKU-001", 10)),
            )

            mockMvc.post("/api/inventories/increase") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    @DisplayName("POST /api/inventories/decrease")
    inner class ReduceBulkInventories {

        @Test
        fun `대량 재고 감소 성공`() {
            val request = BulkReduceStockRequest(
                items = listOf(
                    BulkReduceStockRequest.ReducingStockInfo("SKU-001", 5),
                ),
                idempotencyKey = "key-2",
            )

            mockMvc.post("/api/inventories/decrease") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }

            verify(reduceStockUseCase).execute(any())
        }
    }
}
