package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.AddProductOptionCommand
import com.koosco.catalogservice.application.command.RemoveProductOptionCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.usecase.brand.AddProductOptionUseCase
import com.koosco.catalogservice.application.usecase.product.RemoveProductOptionUseCase
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.SkuStatus
import com.koosco.common.core.event.IntegrationEventProducer
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
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("Product Option UseCase 테스트")
class ProductOptionUseCaseTest {

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var integrationEventProducer: IntegrationEventProducer

    private fun createProductWithOptions(): Product {
        val product = Product(
            id = 1L,
            productCode = "TEST-001",
            name = "테스트 상품",
            price = 10000,
            status = ProductStatus.ACTIVE,
        )
        val group = ProductOptionGroup(id = 1L, name = "색상", ordering = 0)
        val option1 = ProductOption(id = 1L, name = "빨강", additionalPrice = 0, ordering = 0)
        val option2 = ProductOption(id = 2L, name = "파랑", additionalPrice = 0, ordering = 1)
        group.addOption(option1)
        group.addOption(option2)
        product.optionGroups.add(group)
        group.product = product

        // Add an active SKU containing "빨강"
        val sku = ProductSku(
            id = 1L,
            skuId = "TEST-001-빨강-ABC",
            product = product,
            price = 10000,
            optionValues = """{"색상":"빨강"}""",
            status = SkuStatus.ACTIVE,
        )
        product.skus.add(sku)

        return product
    }

    @Nested
    @DisplayName("AddProductOptionUseCase는")
    inner class AddProductOptionUseCaseTest {

        @Test
        fun `옵션을 추가하고 새 SKU를 생성한다`() {
            val useCase = AddProductOptionUseCase(productRepository, integrationEventProducer)
            val product = createProductWithOptions()
            val command = AddProductOptionCommand(
                productId = 1L,
                optionGroupId = 1L,
                options = listOf(
                    AddProductOptionCommand.OptionValueSpec("초록", 500, 2),
                ),
            )

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)
            whenever(productRepository.save(any())).thenAnswer { invocation ->
                val savedProduct = invocation.getArgument<Product>(0)
                // Simulate JPA assigning IDs via reflection
                var nextId = 100L
                savedProduct.optionGroups.forEach { group ->
                    group.options.forEach { option ->
                        if (option.id == null) {
                            try {
                                val idField = ProductOption::class.java.getDeclaredField("id")
                                idField.isAccessible = true
                                idField.set(option, nextId++)
                            } catch (_: Exception) {
                                // ignore
                            }
                        }
                    }
                }
                savedProduct.skus.forEach { sku ->
                    if (sku.id == null) {
                        try {
                            val idField = ProductSku::class.java.getDeclaredField("id")
                            idField.isAccessible = true
                            idField.set(sku, nextId++)
                        } catch (_: Exception) {
                            // ignore
                        }
                    }
                }
                savedProduct
            }
            doNothing().whenever(integrationEventProducer).publish(any())

            val result = useCase.execute(command)

            assertThat(result).isNotNull
            assertThat(result.name).isEqualTo("테스트 상품")
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = AddProductOptionUseCase(productRepository, integrationEventProducer)
            val command = AddProductOptionCommand(1L, 1L, emptyList())

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `옵션 그룹이 없으면 예외를 던진다`() {
            val useCase = AddProductOptionUseCase(productRepository, integrationEventProducer)
            val product = createProductWithOptions()
            val command = AddProductOptionCommand(1L, 999L, emptyList())

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("RemoveProductOptionUseCase는")
    inner class RemoveProductOptionUseCaseTest {

        @Test
        fun `옵션을 제거하고 관련 SKU를 비활성화한다`() {
            val useCase = RemoveProductOptionUseCase(productRepository, integrationEventProducer)
            val product = createProductWithOptions()
            val command = RemoveProductOptionCommand(productId = 1L, optionId = 1L)

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)
            whenever(productRepository.save(any())).thenReturn(product)
            doNothing().whenever(integrationEventProducer).publish(any())

            val result = useCase.execute(command)

            assertThat(result).isNotNull
            // SKU containing "빨강" should be deactivated
            assertThat(product.skus.first().status).isEqualTo(SkuStatus.DEACTIVATED)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = RemoveProductOptionUseCase(productRepository, integrationEventProducer)
            val command = RemoveProductOptionCommand(1L, 1L)

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `옵션이 없으면 예외를 던진다`() {
            val useCase = RemoveProductOptionUseCase(productRepository, integrationEventProducer)
            val product = createProductWithOptions()
            val command = RemoveProductOptionCommand(1L, 999L)

            whenever(productRepository.findByIdWithOptions(1L)).thenReturn(product)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
