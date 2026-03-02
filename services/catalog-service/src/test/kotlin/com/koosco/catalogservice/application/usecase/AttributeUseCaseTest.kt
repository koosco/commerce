package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateCategoryAttributeCommand
import com.koosco.catalogservice.application.command.DeleteCategoryAttributeCommand
import com.koosco.catalogservice.application.command.GetCategoryAttributesCommand
import com.koosco.catalogservice.application.command.SetProductAttributeValuesCommand
import com.koosco.catalogservice.application.command.UpdateCategoryAttributeCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.Category
import com.koosco.catalogservice.domain.entity.CategoryAttribute
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductAttributeValue
import com.koosco.catalogservice.domain.enums.AttributeType
import com.koosco.catalogservice.domain.enums.ProductStatus
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("Attribute UseCase 테스트")
class AttributeUseCaseTest {

    @Mock lateinit var categoryRepository: CategoryRepository

    @Mock lateinit var categoryAttributeRepository: CategoryAttributeRepository

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var productAttributeValueRepository: ProductAttributeValueRepository

    private fun createCategory(id: Long = 1L, name: String = "의류", parent: Category? = null): Category = Category(
        id = id,
        name = name,
        code = "CODE-$id",
        parent = parent,
        depth = if (parent != null) parent.depth + 1 else 0,
    )

    private fun createAttribute(
        id: Long = 1L,
        categoryId: Long = 1L,
        name: String = "색상",
        type: AttributeType = AttributeType.STRING,
        options: String? = null,
        ordering: Int = 0,
    ): CategoryAttribute = CategoryAttribute(
        id = id,
        categoryId = categoryId,
        name = name,
        type = type,
        options = options,
        ordering = ordering,
    )

    private fun createProduct(id: Long = 1L): Product = Product(
        id = id,
        productCode = "TEST-001",
        name = "테스트 상품",
        price = 10000,
        status = ProductStatus.ACTIVE,
    )

    @Nested
    @DisplayName("CreateCategoryAttributeUseCase는")
    inner class CreateCategoryAttributeUseCaseTest {

        @Test
        fun `카테고리에 속성을 추가한다`() {
            val useCase = CreateCategoryAttributeUseCase(categoryRepository, categoryAttributeRepository)
            val command = CreateCategoryAttributeCommand(
                categoryId = 1L,
                name = "색상",
                type = AttributeType.STRING,
                required = true,
                ordering = 1,
            )

            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(createCategory())
            whenever(categoryAttributeRepository.save(any())).thenAnswer { invocation ->
                val attr = invocation.getArgument<CategoryAttribute>(0)
                CategoryAttribute(
                    id = 1L,
                    categoryId = attr.categoryId,
                    name = attr.name,
                    type = attr.type,
                    required = attr.required,
                    options = attr.options,
                    ordering = attr.ordering,
                )
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("색상")
            assertThat(result.type).isEqualTo(AttributeType.STRING)
            assertThat(result.required).isTrue()
        }

        @Test
        fun `카테고리가 없으면 예외를 던진다`() {
            val useCase = CreateCategoryAttributeUseCase(categoryRepository, categoryAttributeRepository)
            val command = CreateCategoryAttributeCommand(
                categoryId = 999L,
                name = "색상",
                type = AttributeType.STRING,
            )

            whenever(categoryRepository.findByIdOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("GetCategoryAttributesUseCase는")
    inner class GetCategoryAttributesUseCaseTest {

        @Test
        fun `카테고리의 속성 목록을 조회한다`() {
            val useCase = GetCategoryAttributesUseCase(categoryRepository, categoryAttributeRepository)
            val command = GetCategoryAttributesCommand(categoryId = 1L, includeInherited = false)
            val attrs = listOf(
                createAttribute(1L, 1L, "색상", AttributeType.STRING),
                createAttribute(2L, 1L, "사이즈", AttributeType.ENUM, "S,M,L"),
            )

            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(createCategory())
            whenever(categoryAttributeRepository.findByCategoryId(1L)).thenReturn(attrs)

            val result = useCase.execute(command)

            assertThat(result).hasSize(2)
            assertThat(result[0].name).isEqualTo("색상")
            assertThat(result[1].name).isEqualTo("사이즈")
        }

        @Test
        fun `상위 카테고리 속성을 상속받아 조회한다`() {
            val useCase = GetCategoryAttributesUseCase(categoryRepository, categoryAttributeRepository)
            val parent = createCategory(1L, "의류")
            val child = createCategory(2L, "상의", parent)
            val command = GetCategoryAttributesCommand(categoryId = 2L, includeInherited = true)

            val parentAttr = createAttribute(1L, 1L, "브랜드", AttributeType.STRING, ordering = 0)
            val childAttr = createAttribute(2L, 2L, "사이즈", AttributeType.ENUM, "S,M,L", ordering = 1)

            whenever(categoryRepository.findByIdOrNull(2L)).thenReturn(child)
            whenever(categoryAttributeRepository.findByCategoryIdIn(listOf(2L, 1L)))
                .thenReturn(listOf(parentAttr, childAttr))

            val result = useCase.execute(command)

            assertThat(result).hasSize(2)
            val inheritedAttr = result.find { it.name == "브랜드" }
            assertThat(inheritedAttr?.inherited).isTrue()
            val ownAttr = result.find { it.name == "사이즈" }
            assertThat(ownAttr?.inherited).isFalse()
        }

        @Test
        fun `카테고리가 없으면 예외를 던진다`() {
            val useCase = GetCategoryAttributesUseCase(categoryRepository, categoryAttributeRepository)
            val command = GetCategoryAttributesCommand(categoryId = 999L)

            whenever(categoryRepository.findByIdOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("UpdateCategoryAttributeUseCase는")
    inner class UpdateCategoryAttributeUseCaseTest {

        @Test
        fun `속성을 수정한다`() {
            val useCase = UpdateCategoryAttributeUseCase(categoryAttributeRepository)
            val attribute = createAttribute(1L, 1L, "색상", AttributeType.STRING)
            val command = UpdateCategoryAttributeCommand(
                attributeId = 1L,
                name = "컬러",
                required = true,
                ordering = 5,
            )

            whenever(categoryAttributeRepository.findOrNull(1L)).thenReturn(attribute)

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("컬러")
        }

        @Test
        fun `속성이 없으면 예외를 던진다`() {
            val useCase = UpdateCategoryAttributeUseCase(categoryAttributeRepository)
            val command = UpdateCategoryAttributeCommand(attributeId = 999L, name = "컬러")

            whenever(categoryAttributeRepository.findOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteCategoryAttributeUseCase는")
    inner class DeleteCategoryAttributeUseCaseTest {

        @Test
        fun `속성을 삭제한다`() {
            val useCase = DeleteCategoryAttributeUseCase(categoryAttributeRepository)
            val attribute = createAttribute(1L)

            whenever(categoryAttributeRepository.findOrNull(1L)).thenReturn(attribute)

            useCase.execute(DeleteCategoryAttributeCommand(1L))

            verify(categoryAttributeRepository).delete(attribute)
        }

        @Test
        fun `속성이 없으면 예외를 던진다`() {
            val useCase = DeleteCategoryAttributeUseCase(categoryAttributeRepository)

            whenever(categoryAttributeRepository.findOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteCategoryAttributeCommand(999L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("SetProductAttributeValuesUseCase는")
    inner class SetProductAttributeValuesUseCaseTest {

        @Test
        fun `상품에 속성 값을 설정한다`() {
            val useCase = SetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )
            val product = createProduct()
            val attribute = createAttribute(1L, 1L, "색상", AttributeType.STRING)
            val command = SetProductAttributeValuesCommand(
                productId = 1L,
                attributes = listOf(
                    SetProductAttributeValuesCommand.AttributeValueSpec(1L, "빨강"),
                ),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(categoryAttributeRepository.findOrNull(1L)).thenReturn(attribute)
            whenever(productAttributeValueRepository.saveAll(any<List<ProductAttributeValue>>()))
                .thenAnswer { invocation ->
                    val values = invocation.getArgument<List<ProductAttributeValue>>(0)
                    values.mapIndexed { index, av ->
                        ProductAttributeValue(
                            id = (index + 1).toLong(),
                            productId = av.productId,
                            attributeId = av.attributeId,
                            value = av.value,
                        )
                    }
                }

            val result = useCase.execute(command)

            assertThat(result).hasSize(1)
            assertThat(result[0].value).isEqualTo("빨강")
            assertThat(result[0].attributeName).isEqualTo("색상")
            verify(productAttributeValueRepository).deleteByProductId(1L)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = SetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )
            val command = SetProductAttributeValuesCommand(
                productId = 999L,
                attributes = listOf(
                    SetProductAttributeValuesCommand.AttributeValueSpec(1L, "빨강"),
                ),
            )

            whenever(productRepository.findOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `속성 정의가 없으면 예외를 던진다`() {
            val useCase = SetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )
            val product = createProduct()
            val command = SetProductAttributeValuesCommand(
                productId = 1L,
                attributes = listOf(
                    SetProductAttributeValuesCommand.AttributeValueSpec(999L, "빨강"),
                ),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(categoryAttributeRepository.findOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("GetProductAttributeValuesUseCase는")
    inner class GetProductAttributeValuesUseCaseTest {

        @Test
        fun `상품의 속성 값을 조회한다`() {
            val useCase = GetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )
            val product = createProduct()
            val attribute = createAttribute(1L, 1L, "색상", AttributeType.STRING)
            val attrValue = ProductAttributeValue(
                id = 1L,
                productId = 1L,
                attributeId = 1L,
                value = "빨강",
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(productAttributeValueRepository.findByProductId(1L)).thenReturn(listOf(attrValue))
            whenever(categoryAttributeRepository.findOrNull(1L)).thenReturn(attribute)

            val result = useCase.execute(1L)

            assertThat(result).hasSize(1)
            assertThat(result[0].value).isEqualTo("빨강")
            assertThat(result[0].attributeName).isEqualTo("색상")
        }

        @Test
        fun `속성 값이 없으면 빈 리스트를 반환한다`() {
            val useCase = GetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(createProduct())
            whenever(productAttributeValueRepository.findByProductId(1L)).thenReturn(emptyList())

            val result = useCase.execute(1L)

            assertThat(result).isEmpty()
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = GetProductAttributeValuesUseCase(
                productRepository,
                categoryAttributeRepository,
                productAttributeValueRepository,
            )

            whenever(productRepository.findOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(999L) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
