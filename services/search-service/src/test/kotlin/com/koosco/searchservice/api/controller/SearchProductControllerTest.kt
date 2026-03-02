package com.koosco.searchservice.api.controller

import com.koosco.searchservice.application.result.SearchProductResult
import com.koosco.searchservice.application.usecase.search.SearchProductUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class SearchProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var searchProductUseCase: SearchProductUseCase

    @Test
    fun `키워드로 상품 검색 시 200 응답을 반환한다`() {
        // given
        val results = listOf(
            SearchProductResult(
                productId = 1L,
                name = "스마트폰",
                sellingPrice = 45000,
                thumbnailImageUrl = "https://example.com/image.jpg",
                averageRating = 4.5,
                reviewCount = 10,
            ),
        )
        whenever(searchProductUseCase.execute(any())).thenReturn(
            PageImpl(results, PageRequest.of(0, 20), 1),
        )

        // when & then
        mockMvc.get("/api/search/products") {
            param("keyword", "스마트폰")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.content[0].productId") { value(1) }
            jsonPath("$.data.content[0].name") { value("스마트폰") }
            jsonPath("$.data.content[0].sellingPrice") { value(45000) }
            jsonPath("$.data.totalElements") { value(1) }
        }
    }

    @Test
    fun `필터 조건을 적용하여 검색할 수 있다`() {
        // given
        whenever(searchProductUseCase.execute(any())).thenReturn(
            PageImpl(emptyList(), PageRequest.of(0, 20), 0),
        )

        // when & then
        mockMvc.get("/api/search/products") {
            param("keyword", "노트북")
            param("categoryId", "5")
            param("brandId", "2")
            param("minPrice", "500000")
            param("maxPrice", "2000000")
            param("sort", "PRICE_ASC")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.totalElements") { value(0) }
        }
    }

    @Test
    fun `키워드 없이 검색하면 전체 결과를 반환한다`() {
        // given
        whenever(searchProductUseCase.execute(any())).thenReturn(
            PageImpl(emptyList(), PageRequest.of(0, 20), 0),
        )

        // when & then
        mockMvc.get("/api/search/products")
            .andExpect {
                status { isOk() }
            }
    }
}
