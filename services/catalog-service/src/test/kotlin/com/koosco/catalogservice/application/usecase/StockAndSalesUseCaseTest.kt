package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.ProductLikeRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductLike
import com.koosco.catalogservice.domain.entity.ProductLikeId
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.SkuStatus
import com.koosco.common.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("Stock/Sales/Like UseCase 테스트")
class StockAndSalesUseCaseTest {

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var idempotencyRepository: CatalogIdempotencyRepository

    @Mock lateinit var productLikeRepository: ProductLikeRepository

    private fun createProduct(status: ProductStatus = ProductStatus.ACTIVE): Product {
        val product = Product(
            id = 1L,
            productCode = "TEST-001",
            name = "테스트",
            price = 10000,
            status = status,
        )
        product.skus.add(
            ProductSku(
                id = 1L,
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = "{}",
                status = SkuStatus.ACTIVE,
            ),
        )
        return product
    }

    @Nested
    @DisplayName("UpdateProductStockStatusUseCase는")
    inner class UpdateProductStockStatusUseCaseTest {

        @Test
        fun `재고 소진 시 OUT_OF_STOCK으로 변경한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.ACTIVE)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markOutOfStock("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.OUT_OF_STOCK)
        }

        @Test
        fun `이미 OUT_OF_STOCK이면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.OUT_OF_STOCK)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markOutOfStock("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.OUT_OF_STOCK)
        }

        @Test
        fun `상품이 없으면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(null)

            useCase.markOutOfStock("SKU-001") // 예외 없이 통과
        }

        @Test
        fun `전이 불가능한 상태이면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.DELETED)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markOutOfStock("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }

        @Test
        fun `재고 복구 시 ACTIVE로 변경한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.OUT_OF_STOCK)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markActive("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.ACTIVE)
        }

        @Test
        fun `이미 ACTIVE이면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.ACTIVE)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markActive("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.ACTIVE)
        }

        @Test
        fun `markActive에서 상품이 없으면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(null)

            useCase.markActive("SKU-001") // 예외 없이 통과
        }

        @Test
        fun `markActive에서 전이 불가능한 상태이면 무시한다`() {
            val useCase = UpdateProductStockStatusUseCase(productRepository)
            val product = createProduct(ProductStatus.DELETED)

            whenever(productRepository.findBySkuId("SKU-001")).thenReturn(product)

            useCase.markActive("SKU-001")

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }
    }

    @Nested
    @DisplayName("UpdateProductSalesCountUseCase는")
    inner class UpdateProductSalesCountUseCaseTest {

        @Test
        fun `판매 수를 증가시킨다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)
            val product = createProduct()

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(null)
            whenever(productRepository.findBySkuId("1")).thenReturn(product)
            whenever(idempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            useCase.incrementSalesCount(100L, 1L, 5)

            assertThat(product.salesCount).isEqualTo(5)
        }

        @Test
        fun `중복 요청이면 무시한다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)
            val existing = CatalogIdempotency.create("order-confirmed:100:1", "SALES_COUNT", 1L)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(existing)

            useCase.incrementSalesCount(100L, 1L, 5) // 중복이라 무시됨
        }

        @Test
        fun `상품이 없으면 무시한다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(null)
            whenever(productRepository.findBySkuId("1")).thenReturn(null)

            useCase.incrementSalesCount(100L, 1L, 5) // 상품이 없으면 무시
        }

        @Test
        fun `판매 수를 감소시킨다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)
            val product = createProduct()
            product.incrementSalesCount(10)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(null)
            whenever(productRepository.findBySkuId("1")).thenReturn(product)
            whenever(idempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            useCase.decrementSalesCount(100L, 1L, 3)

            assertThat(product.salesCount).isEqualTo(7)
        }

        @Test
        fun `decrementSalesCount에서 중복 요청이면 무시한다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)
            val existing = CatalogIdempotency.create("order-cancelled:100:1", "SALES_COUNT", 1L)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(existing)

            useCase.decrementSalesCount(100L, 1L, 3)
        }

        @Test
        fun `decrementSalesCount에서 상품이 없으면 무시한다`() {
            val useCase = UpdateProductSalesCountUseCase(productRepository, idempotencyRepository)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType(any(), any())).thenReturn(null)
            whenever(productRepository.findBySkuId("1")).thenReturn(null)

            useCase.decrementSalesCount(100L, 1L, 3)
        }
    }

    @Nested
    @DisplayName("ToggleProductLikeUseCase는")
    inner class ToggleProductLikeUseCaseTest {

        @Test
        fun `좋아요를 추가한다`() {
            val useCase = ToggleProductLikeUseCase(productRepository, productLikeRepository, idempotencyRepository)
            val product = createProduct()

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(productLikeRepository.findById(ProductLikeId(1L, 1L))).thenReturn(null)
            whenever(productLikeRepository.save(any())).thenReturn(ProductLike(1L, 1L))

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isTrue()
            assertThat(product.likeCount).isEqualTo(1)
        }

        @Test
        fun `좋아요를 취소한다`() {
            val useCase = ToggleProductLikeUseCase(productRepository, productLikeRepository, idempotencyRepository)
            val product = createProduct()
            product.likeCount = 1
            val existing = ProductLike(1L, 1L)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(productLikeRepository.findById(ProductLikeId(1L, 1L))).thenReturn(existing)

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isFalse()
            assertThat(product.likeCount).isEqualTo(0)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = ToggleProductLikeUseCase(productRepository, productLikeRepository, idempotencyRepository)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(1L, 1L) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키가 있고 이미 처리된 요청이면 기존 결과를 반환한다`() {
            val useCase = ToggleProductLikeUseCase(productRepository, productLikeRepository, idempotencyRepository)
            val existing = CatalogIdempotency.create("like-key", "PRODUCT_LIKE", 1L)

            whenever(idempotencyRepository.findByIdempotencyKeyAndResourceType("like-key", "PRODUCT_LIKE"))
                .thenReturn(existing)

            val liked = useCase.execute(1L, 1L, "like-key")

            assertThat(liked).isTrue() // resourceId == 1L -> true
        }
    }
}
