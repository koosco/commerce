package com.koosco.inventoryservice.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.inventoryservice.application.usecase.InventorySeedUseCase
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(TestInventoryStockController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
@DisplayName("TestInventoryStockController")
class TestInventoryStockControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var inventorySeedUseCase: InventorySeedUseCase

    @MockitoBean
    lateinit var reduceStockUseCase: ReduceStockUseCase

    @Nested
    @DisplayName("POST /api/inventories/test/init")
    inner class InitStock {

        @Test
        fun `재고 초기화 성공`() {
            mockMvc.post("/api/inventories/test/init")
                .andExpect {
                    status { isOk() }
                }

            verify(inventorySeedUseCase).init()
        }
    }

    @Nested
    @DisplayName("POST /api/inventories/test/clear")
    inner class ClearStock {

        @Test
        fun `재고 클리어 성공`() {
            mockMvc.post("/api/inventories/test/clear")
                .andExpect {
                    status { isOk() }
                }

            verify(inventorySeedUseCase).clear()
        }
    }

    @Nested
    @DisplayName("POST /api/inventories/test/decrease/{skuId}")
    inner class DecreaseStock {

        @Test
        fun `재고 감소 성공`() {
            val request = TestInventoryStockController.StockDto(quantity = 5)

            mockMvc.post("/api/inventories/test/decrease/SKU-001") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }

            verify(reduceStockUseCase).execute(any())
        }
    }
}
