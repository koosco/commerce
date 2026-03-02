package com.koosco.searchservice.application.usecase.sync

import com.koosco.searchservice.contract.inbound.catalog.ProductChangedEvent
import com.koosco.searchservice.contract.inbound.catalog.ProductDeletedEvent
import com.koosco.searchservice.domain.entity.SearchProduct
import com.koosco.searchservice.domain.repository.SearchProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class SearchProductSyncUseCaseTest {

    @Mock
    private lateinit var searchProductRepository: SearchProductRepository

    @InjectMocks
    private lateinit var useCase: SearchProductSyncUseCase

    @Nested
    @DisplayName("upsert")
    inner class Upsert {

        @Test
        @DisplayName("새 상품이면 SearchProduct를 생성한다")
        fun `creates new search product when not exists`() {
            // given
            val event = createProductChangedEvent(productId = 1L)
            whenever(searchProductRepository.findByProductId(1L)).thenReturn(null)
            whenever(searchProductRepository.save(any<SearchProduct>())).thenAnswer { it.arguments[0] }

            // when
            useCase.upsert(event)

            // then
            val captor = argumentCaptor<SearchProduct>()
            verify(searchProductRepository).save(captor.capture())

            val saved = captor.firstValue
            assertThat(saved.productId).isEqualTo(1L)
            assertThat(saved.name).isEqualTo("Test Product")
            assertThat(saved.price).isEqualTo(10000L)
            assertThat(saved.sellingPrice).isEqualTo(8000L)
            assertThat(saved.status).isEqualTo("ACTIVE")
        }

        @Test
        @DisplayName("기존 상품이면 SearchProduct를 업데이트한다")
        fun `updates existing search product`() {
            // given
            val existing = SearchProduct(
                id = 1L,
                productId = 1L,
                name = "Old Name",
                price = 5000L,
                sellingPrice = 4000L,
                status = "INACTIVE",
            )
            val event = createProductChangedEvent(productId = 1L)
            whenever(searchProductRepository.findByProductId(1L)).thenReturn(existing)

            // when
            useCase.upsert(event)

            // then
            verify(searchProductRepository, never()).save(any<SearchProduct>())
            assertThat(existing.name).isEqualTo("Test Product")
            assertThat(existing.price).isEqualTo(10000L)
            assertThat(existing.sellingPrice).isEqualTo(8000L)
            assertThat(existing.status).isEqualTo("ACTIVE")
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("상품이 존재하면 삭제한다")
        fun `deletes existing search product`() {
            // given
            val existing = SearchProduct(
                id = 1L,
                productId = 1L,
                name = "Test",
                price = 10000L,
                sellingPrice = 8000L,
                status = "ACTIVE",
            )
            whenever(searchProductRepository.findByProductId(1L)).thenReturn(existing)

            // when
            useCase.delete(ProductDeletedEvent(productId = 1L))

            // then
            verify(searchProductRepository).deleteByProductId(1L)
        }

        @Test
        @DisplayName("상품이 없으면 무시한다 (멱등성)")
        fun `ignores when product does not exist`() {
            // given
            whenever(searchProductRepository.findByProductId(99L)).thenReturn(null)

            // when
            useCase.delete(ProductDeletedEvent(productId = 99L))

            // then
            verify(searchProductRepository, never()).deleteByProductId(any())
        }
    }

    private fun createProductChangedEvent(productId: Long) = ProductChangedEvent(
        productId = productId,
        name = "Test Product",
        description = "Test Description",
        price = 10000L,
        sellingPrice = 8000L,
        categoryId = 1L,
        categoryName = "Electronics",
        brandId = 1L,
        brandName = "TestBrand",
        thumbnailImageUrl = "https://example.com/image.jpg",
        status = "ACTIVE",
    )
}
