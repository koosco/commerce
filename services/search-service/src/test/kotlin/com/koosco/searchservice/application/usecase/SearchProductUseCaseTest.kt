package com.koosco.searchservice.application.usecase

import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.application.port.SearchProductRepository
import com.koosco.searchservice.application.usecase.search.SearchProductUseCase
import com.koosco.searchservice.domain.entity.SearchProduct
import com.koosco.searchservice.domain.enums.SearchSortStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockitoExtension::class)
class SearchProductUseCaseTest {

    @Mock
    private lateinit var searchProductRepository: SearchProductRepository

    @InjectMocks
    private lateinit var searchProductUseCase: SearchProductUseCase

    @Test
    fun `키워드로 상품을 검색하면 결과를 반환한다`() {
        // given
        val command = SearchProductCommand(
            keyword = "스마트폰",
            categoryId = null,
            brandId = null,
            minPrice = null,
            maxPrice = null,
            sort = SearchSortStrategy.RELEVANCE,
            pageable = PageRequest.of(0, 20),
        )

        val searchProducts = listOf(
            createSearchProduct(1L, "스마트폰 케이스", 15000),
            createSearchProduct(2L, "스마트폰 충전기", 25000),
        )
        whenever(searchProductRepository.search(any())).thenReturn(
            PageImpl(searchProducts, command.pageable, 2),
        )

        // when
        val result = searchProductUseCase.execute(command)

        // then
        assertEquals(2, result.totalElements)
        assertEquals(2, result.content.size)
        assertEquals(1L, result.content[0].productId)
        assertEquals("스마트폰 케이스", result.content[0].name)
        assertEquals(15000L, result.content[0].sellingPrice)
    }

    @Test
    fun `키워드 없이 검색하면 전체 ACTIVE 상품을 반환한다`() {
        // given
        val command = SearchProductCommand(
            keyword = null,
            categoryId = null,
            brandId = null,
            minPrice = null,
            maxPrice = null,
            sort = SearchSortStrategy.LATEST,
            pageable = PageRequest.of(0, 20),
        )

        val searchProducts = listOf(
            createSearchProduct(1L, "상품A", 10000),
            createSearchProduct(2L, "상품B", 20000),
            createSearchProduct(3L, "상품C", 30000),
        )
        whenever(searchProductRepository.search(any())).thenReturn(
            PageImpl(searchProducts, command.pageable, 3),
        )

        // when
        val result = searchProductUseCase.execute(command)

        // then
        assertEquals(3, result.totalElements)
    }

    @Test
    fun `카테고리와 가격 범위 필터를 적용하여 검색한다`() {
        // given
        val command = SearchProductCommand(
            keyword = "노트북",
            categoryId = 5L,
            brandId = null,
            minPrice = 500000,
            maxPrice = 2000000,
            sort = SearchSortStrategy.PRICE_ASC,
            pageable = PageRequest.of(0, 10),
        )

        val searchProducts = listOf(
            createSearchProduct(10L, "노트북 A", 800000),
        )
        whenever(searchProductRepository.search(any())).thenReturn(
            PageImpl(searchProducts, command.pageable, 1),
        )

        // when
        val result = searchProductUseCase.execute(command)

        // then
        assertEquals(1, result.totalElements)
        assertEquals(10L, result.content[0].productId)
    }

    @Test
    fun `검색 결과가 없으면 빈 페이지를 반환한다`() {
        // given
        val command = SearchProductCommand(
            keyword = "존재하지않는상품",
            categoryId = null,
            brandId = null,
            minPrice = null,
            maxPrice = null,
            sort = SearchSortStrategy.RELEVANCE,
            pageable = PageRequest.of(0, 20),
        )

        whenever(searchProductRepository.search(any())).thenReturn(
            PageImpl(emptyList(), command.pageable, 0),
        )

        // when
        val result = searchProductUseCase.execute(command)

        // then
        assertEquals(0, result.totalElements)
        assertEquals(0, result.content.size)
    }

    private fun createSearchProduct(productId: Long, name: String, sellingPrice: Long): SearchProduct = SearchProduct(
        productId = productId,
        name = name,
        description = "$name 설명",
        price = sellingPrice,
        sellingPrice = sellingPrice,
        status = "ACTIVE",
        averageRating = 4.5,
        reviewCount = 10,
    )
}
