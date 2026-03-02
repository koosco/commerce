package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.ChangeProductStatusCommand
import com.koosco.catalogservice.application.command.CreateProductCommand
import com.koosco.catalogservice.application.command.DeleteProductCommand
import com.koosco.catalogservice.application.command.FindSkuCommand
import com.koosco.catalogservice.application.command.GetProductDetailCommand
import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.command.UpdateProductCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.port.UserBehaviorEventProducer
import com.koosco.catalogservice.application.usecase.product.ChangeProductStatusUseCase
import com.koosco.catalogservice.application.usecase.product.CreateProductUseCase
import com.koosco.catalogservice.application.usecase.product.DeleteProductUseCase
import com.koosco.catalogservice.application.usecase.product.FindSkuUseCase
import com.koosco.catalogservice.application.usecase.product.GetProductDetailUseCase
import com.koosco.catalogservice.application.usecase.product.GetProductListUseCase
import com.koosco.catalogservice.application.usecase.product.UpdateProductUseCase
import com.koosco.catalogservice.domain.entity.Brand
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.entity.Promotion
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.PromotionType
import com.koosco.catalogservice.domain.enums.SkuStatus
import com.koosco.catalogservice.domain.enums.SortStrategy
import com.koosco.catalogservice.domain.service.ProductValidator
import com.koosco.catalogservice.domain.service.SkuGenerator
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.BadRequestException
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
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("Product UseCase 테스트")
class ProductUseCaseTest {

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var categoryRepository: CategoryRepository

    @Mock lateinit var brandRepository: BrandRepository

    @Mock lateinit var promotionRepository: PromotionRepository

    @Mock lateinit var skuGenerator: SkuGenerator

    @Mock lateinit var productValidator: ProductValidator

    @Mock lateinit var integrationEventProducer: IntegrationEventProducer

    @Mock lateinit var catalogIdempotencyRepository: CatalogIdempotencyRepository

    @Mock lateinit var userBehaviorEventProducer: UserBehaviorEventProducer

    private fun createProduct(id: Long = 1L, status: ProductStatus = ProductStatus.ACTIVE): Product {
        val product = Product(
            id = id,
            productCode = "TEST-001",
            name = "테스트 상품",
            price = 10000,
            status = status,
            brandId = 1L,
        )
        return product
    }

    @Nested
    @DisplayName("CreateProductUseCase는")
    inner class CreateProductUseCaseTest {

        @Test
        fun `상품을 생성한다`() {
            val useCase = CreateProductUseCase(
                productRepository,
                categoryRepository,
                skuGenerator,
                productValidator,
                integrationEventProducer,
                catalogIdempotencyRepository,
            )
            val command = CreateProductCommand(
                name = "새 상품",
                description = "설명",
                price = 10000,
                status = ProductStatus.DRAFT,
                categoryId = null,
                thumbnailImageUrl = null,
                brandId = null,
                optionGroups = emptyList(),
            )

            whenever(productRepository.save(any())).thenAnswer { invocation ->
                val p = invocation.getArgument<Product>(0)
                Product(
                    id = 1L,
                    productCode = p.productCode,
                    name = p.name,
                    price = p.price,
                    status = p.status,
                )
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("새 상품")
            verify(productValidator).validateSkuCount(any())
        }

        @Test
        fun `멱등성 키가 있으면 기존 상품을 반환한다`() {
            val useCase = CreateProductUseCase(
                productRepository,
                categoryRepository,
                skuGenerator,
                productValidator,
                integrationEventProducer,
                catalogIdempotencyRepository,
            )
            val product = createProduct()
            val existing = CatalogIdempotency.create("key-1", "PRODUCT", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "PRODUCT"))
                .thenReturn(existing)
            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            val result = useCase.execute(
                CreateProductCommand("새 상품", null, 10000, ProductStatus.DRAFT, null, null, null, emptyList()),
                idempotencyKey = "key-1",
            )

            assertThat(result.id).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 상품이 없으면 예외를 던진다`() {
            val useCase = CreateProductUseCase(
                productRepository,
                categoryRepository,
                skuGenerator,
                productValidator,
                integrationEventProducer,
                catalogIdempotencyRepository,
            )
            val existing = CatalogIdempotency.create("key-1", "PRODUCT", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "PRODUCT"))
                .thenReturn(existing)
            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(
                    CreateProductCommand("새 상품", null, 10000, ProductStatus.DRAFT, null, null, null, emptyList()),
                    idempotencyKey = "key-1",
                )
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `옵션 그룹을 포함한 상품을 생성한다`() {
            val useCase = CreateProductUseCase(
                productRepository,
                categoryRepository,
                skuGenerator,
                productValidator,
                integrationEventProducer,
                catalogIdempotencyRepository,
            )
            val command = CreateProductCommand(
                name = "옵션 상품",
                description = "설명",
                price = 10000,
                status = ProductStatus.DRAFT,
                categoryId = null,
                thumbnailImageUrl = null,
                brandId = null,
                optionGroups = listOf(
                    CreateProductCommand.ProductOptionGroup(
                        "색상",
                        0,
                        listOf(CreateProductCommand.ProductOption("빨강", 0, 0)),
                    ),
                ),
            )

            whenever(productRepository.save(any())).thenAnswer { invocation ->
                val p = invocation.getArgument<Product>(0)
                Product(
                    id = 1L,
                    productCode = p.productCode,
                    name = p.name,
                    price = p.price,
                    status = p.status,
                )
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("옵션 상품")
            verify(productValidator).validateSkuCount(any())
            verify(productValidator).validateOptionGroupStructure(any())
        }

        @Test
        fun `멱등성 키와 함께 상품을 생성하면 멱등성 레코드를 저장한다`() {
            val useCase = CreateProductUseCase(
                productRepository,
                categoryRepository,
                skuGenerator,
                productValidator,
                integrationEventProducer,
                catalogIdempotencyRepository,
            )
            val command = CreateProductCommand("새 상품", null, 10000, ProductStatus.DRAFT, null, null, null, emptyList())

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "PRODUCT"))
                .thenReturn(null)
            whenever(productRepository.save(any())).thenAnswer { invocation ->
                val p = invocation.getArgument<Product>(0)
                Product(id = 1L, productCode = p.productCode, name = p.name, price = p.price, status = p.status)
            }
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.name).isEqualTo("새 상품")
            verify(catalogIdempotencyRepository).save(any())
        }
    }

    @Nested
    @DisplayName("GetProductDetailUseCase는")
    inner class GetProductDetailUseCaseTest {

        @Test
        fun `상품 상세를 조회한다`() {
            val useCase = GetProductDetailUseCase(
                productRepository,
                brandRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val product = createProduct()
            val brand = Brand(id = 1L, name = "브랜드A")

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)
            whenever(brandRepository.findOrNull(1L)).thenReturn(brand)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(emptyList())

            val result = useCase.execute(GetProductDetailCommand(1L))

            assertThat(result.name).isEqualTo("테스트 상품")
            assertThat(result.brandName).isEqualTo("브랜드A")
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = GetProductDetailUseCase(
                productRepository,
                brandRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(GetProductDetailCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `userId가 있으면 조회 이벤트를 발행한다`() {
            val useCase = GetProductDetailUseCase(
                productRepository,
                brandRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val product = createProduct()

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)
            whenever(brandRepository.findOrNull(any())).thenReturn(null)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(emptyList())

            useCase.execute(GetProductDetailCommand(1L, userId = 100L))

            verify(userBehaviorEventProducer).publish(any())
        }

        @Test
        fun `활성 프로모션이 있으면 할인가를 적용한다`() {
            val useCase = GetProductDetailUseCase(
                productRepository,
                brandRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val product = createProduct()
            val now = LocalDateTime.now()
            val promotion = Promotion.create(1L, 7000, now.minusDays(1), now.plusDays(1), PromotionType.CAMPAIGN, 0)

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)
            whenever(brandRepository.findOrNull(any())).thenReturn(null)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(listOf(promotion))

            val result = useCase.execute(GetProductDetailCommand(1L))

            assertThat(result.sellingPrice).isEqualTo(7000)
        }
    }

    @Nested
    @DisplayName("GetProductListUseCase는")
    inner class GetProductListUseCaseTest {

        @Test
        fun `상품 목록을 조회한다`() {
            val useCase = GetProductListUseCase(
                productRepository,
                brandRepository,
                categoryRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val products = listOf(createProduct())
            val page = PageImpl(products)
            val command = GetProductListCommand(
                categoryId = null,
                keyword = null,
                brandId = null,
                minPrice = null,
                maxPrice = null,
                sort = SortStrategy.LATEST,
                pageable = PageRequest.of(0, 10),
            )

            whenever(productRepository.search(any())).thenReturn(page)
            whenever(promotionRepository.findActiveByProductIds(any(), any())).thenReturn(emptyList())

            val result = useCase.execute(command)

            assertThat(result.content).hasSize(1)
        }

        @Test
        fun `카테고리 ID가 있으면 하위 카테고리를 포함하여 조회한다`() {
            val useCase = GetProductListUseCase(
                productRepository,
                brandRepository,
                categoryRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val command = GetProductListCommand(
                categoryId = 1L,
                keyword = null,
                brandId = null,
                minPrice = null,
                maxPrice = null,
                sort = SortStrategy.LATEST,
                pageable = PageRequest.of(0, 10),
            )
            val page = PageImpl(listOf(createProduct()))

            whenever(categoryRepository.findDescendantIds(1L)).thenReturn(listOf(1L, 2L, 3L))
            whenever(productRepository.search(any())).thenReturn(page)
            whenever(promotionRepository.findActiveByProductIds(any(), any())).thenReturn(emptyList())

            val result = useCase.execute(command)

            assertThat(result.content).hasSize(1)
            verify(categoryRepository).findDescendantIds(1L)
        }

        @Test
        fun `브랜드 정보를 매핑한다`() {
            val useCase = GetProductListUseCase(
                productRepository,
                brandRepository,
                categoryRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val product = createProduct()
            val page = PageImpl(listOf(product))
            val command = GetProductListCommand(
                categoryId = null,
                keyword = null,
                brandId = null,
                minPrice = null,
                maxPrice = null,
                sort = SortStrategy.LATEST,
                pageable = PageRequest.of(0, 10),
            )
            val brand = Brand(id = 1L, name = "브랜드A")

            whenever(productRepository.search(any())).thenReturn(page)
            whenever(brandRepository.findAllByIdIn(any())).thenReturn(listOf(brand))
            whenever(promotionRepository.findActiveByProductIds(any(), any())).thenReturn(emptyList())

            val result = useCase.execute(command)

            assertThat(result.content.first().brandName).isEqualTo("브랜드A")
        }

        @Test
        fun `userId와 keyword가 있으면 검색 이벤트를 발행한다`() {
            val useCase = GetProductListUseCase(
                productRepository,
                brandRepository,
                categoryRepository,
                promotionRepository,
                userBehaviorEventProducer,
            )
            val page = PageImpl(emptyList<Product>())
            val command = GetProductListCommand(
                categoryId = null,
                keyword = "키워드",
                brandId = null,
                minPrice = null,
                maxPrice = null,
                sort = SortStrategy.LATEST,
                pageable = PageRequest.of(0, 10),
                userId = 100L,
            )

            whenever(productRepository.search(any())).thenReturn(page)

            useCase.execute(command)

            verify(userBehaviorEventProducer).publish(any())
        }
    }

    @Nested
    @DisplayName("UpdateProductUseCase는")
    inner class UpdateProductUseCaseTest {

        @Test
        fun `상품을 수정한다`() {
            val useCase = UpdateProductUseCase(productRepository)
            val product = createProduct()
            val command = UpdateProductCommand(1L, "변경된 이름", null, null, null, null, null)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            useCase.execute(command)

            assertThat(product.name).isEqualTo("변경된 이름")
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = UpdateProductUseCase(productRepository)
            val command = UpdateProductCommand(1L, "변경", null, null, null, null, null)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteProductUseCase는")
    inner class DeleteProductUseCaseTest {

        @Test
        fun `상품을 삭제한다`() {
            val useCase = DeleteProductUseCase(productRepository)
            val product = createProduct(status = ProductStatus.ACTIVE)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            useCase.execute(DeleteProductCommand(1L))

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = DeleteProductUseCase(productRepository)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteProductCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("ChangeProductStatusUseCase는")
    inner class ChangeProductStatusUseCaseTest {

        @Test
        fun `상태를 변경한다`() {
            val useCase = ChangeProductStatusUseCase(productRepository, integrationEventProducer)
            val product = createProduct(status = ProductStatus.ACTIVE)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            doNothing().whenever(integrationEventProducer).publish(any())

            useCase.execute(ChangeProductStatusCommand(1L, ProductStatus.SUSPENDED))

            assertThat(product.status).isEqualTo(ProductStatus.SUSPENDED)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = ChangeProductStatusUseCase(productRepository, integrationEventProducer)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(ChangeProductStatusCommand(1L, ProductStatus.SUSPENDED)) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `잘못된 상태 전이이면 BadRequestException을 던진다`() {
            val useCase = ChangeProductStatusUseCase(productRepository, integrationEventProducer)
            val product = createProduct(status = ProductStatus.DELETED)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            assertThatThrownBy { useCase.execute(ChangeProductStatusCommand(1L, ProductStatus.ACTIVE)) }
                .isInstanceOf(BadRequestException::class.java)
        }
    }

    @Nested
    @DisplayName("FindSkuUseCase는")
    inner class FindSkuUseCaseTest {

        @Test
        fun `옵션에 맞는 SKU를 찾는다`() {
            val useCase = FindSkuUseCase(productRepository)
            val product = createProduct()
            val sku = ProductSku(
                id = 1L,
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = """{"색상":"빨강"}""",
                status = SkuStatus.ACTIVE,
            )
            product.skus.add(sku)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            val result = useCase.execute(FindSkuCommand(1L, mapOf("색상" to "빨강")))

            assertThat(result.skuId).isEqualTo("SKU-001")
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = FindSkuUseCase(productRepository)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(FindSkuCommand(1L, mapOf("색상" to "빨강"))) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `일치하는 SKU가 없으면 예외를 던진다`() {
            val useCase = FindSkuUseCase(productRepository)
            val product = createProduct()

            whenever(productRepository.findOrNull(1L)).thenReturn(product)

            assertThatThrownBy { useCase.execute(FindSkuCommand(1L, mapOf("색상" to "빨강"))) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
