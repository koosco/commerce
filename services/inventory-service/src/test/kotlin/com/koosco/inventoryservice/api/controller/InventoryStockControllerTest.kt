package com.koosco.inventoryservice.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.inventoryservice.api.request.BulkAddStockRequest
import com.koosco.inventoryservice.api.request.BulkReduceStockRequest
import com.koosco.inventoryservice.api.request.ReserveStockRequest
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.usecase.AddStockUseCase
import com.koosco.inventoryservice.application.usecase.ReduceStockUseCase
import com.koosco.inventoryservice.application.usecase.ReserveStockUseCase
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

    @MockitoBean
    lateinit var reserveStockUseCase: ReserveStockUseCase

    @MockitoBean
    lateinit var inventoryApiIdempotencyRepository: InventoryApiIdempotencyRepository

    @Nested
    @DisplayName("POST /internal/inventories/increase")
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

            mockMvc.post("/internal/inventories/increase") {
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

            mockMvc.post("/internal/inventories/increase") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    @DisplayName("POST /internal/inventories/decrease")
    inner class ReduceBulkInventories {

        @Test
        fun `대량 재고 감소 성공`() {
            val request = BulkReduceStockRequest(
                items = listOf(
                    BulkReduceStockRequest.ReducingStockInfo("SKU-001", 5),
                ),
                idempotencyKey = "key-2",
            )

            mockMvc.post("/internal/inventories/decrease") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }

            verify(reduceStockUseCase).execute(any())
        }
    }

    @Nested
    @DisplayName("POST /internal/inventories/reserve")
    inner class ReserveStock {

        @Test
        fun `주문 재고 예약 성공`() {
            val request = ReserveStockRequest(
                orderId = 100L,
                items = listOf(
                    ReserveStockRequest.ReserveItemInfo("SKU-001", 2),
                ),
                idempotencyKey = "reserve-key-1",
                correlationId = "corr-100",
            )

            mockMvc.post("/internal/inventories/reserve") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }

            verify(reserveStockUseCase).execute(any(), any(), any())
        }
    }
}
