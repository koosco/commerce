package com.koosco.catalogservice.product.domain.service

import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.service.SkuGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SkuGenerator 서비스 테스트")
class SkuGeneratorTest {

    private val skuGenerator = SkuGenerator()

    private fun createTestProduct(productCode: String = "TEST-20250108-X1Y2", price: Long = 10000): Product = Product(
        productCode = productCode,
        name = "테스트 상품",
        price = price,
        status = ProductStatus.ACTIVE,
    )

    private fun createOptionGroup(name: String, ordering: Int, options: List<Pair<String, Long>>): ProductOptionGroup {
        val optionGroup = ProductOptionGroup(
            name = name,
            ordering = ordering,
        )

        options.forEachIndexed { index, (optionName, additionalPrice) ->
            val option = ProductOption(
                name = optionName,
                additionalPrice = additionalPrice,
                ordering = index,
            )
            optionGroup.addOption(option)
        }

        return optionGroup
    }

    @Nested
    @DisplayName("generateSkus 메서드는")
    inner class GenerateSkusTest {

        @Test
        @DisplayName("옵션 그룹이 없을 때 SKU를 생성하지 않는다")
        fun `should not generate SKUs when no option groups`() {
            // Given
            val product = createTestProduct()

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).isEmpty()
        }

        @Test
        @DisplayName("단일 옵션 그룹으로 SKU를 생성한다")
        fun `should generate SKUs with single option group`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "RED" to 0L,
                    "BLUE" to 0L,
                    "BLACK" to 0L,
                ),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(3)
            assertThat(product.skus.map { it.price }).allMatch { it == 10000L }
        }

        @Test
        @DisplayName("두 개의 옵션 그룹으로 데카르트 곱 SKU를 생성한다")
        fun `should generate cartesian product SKUs with two option groups`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "RED" to 0L,
                    "BLUE" to 0L,
                ),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf(
                    "S" to 0L,
                    "M" to 1000L,
                    "L" to 2000L,
                ),
            )

            product.optionGroups.add(colorGroup)
            product.optionGroups.add(sizeGroup)
            colorGroup.product = product
            sizeGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            // 2 colors x 3 sizes = 6 SKUs
            assertThat(product.skus).hasSize(6)
        }

        @Test
        @DisplayName("세 개의 옵션 그룹으로 데카르트 곱 SKU를 생성한다")
        fun `should generate cartesian product SKUs with three option groups`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L, "BLUE" to 0L),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf("S" to 0L, "M" to 0L),
            )
            val materialGroup = createOptionGroup(
                name = "MATERIAL",
                ordering = 2,
                options = listOf("COTTON" to 0L, "POLYESTER" to 0L),
            )

            product.optionGroups.addAll(listOf(colorGroup, sizeGroup, materialGroup))
            colorGroup.product = product
            sizeGroup.product = product
            materialGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            // 2 colors x 2 sizes x 2 materials = 8 SKUs
            assertThat(product.skus).hasSize(8)
        }

        @Test
        @DisplayName("옵션 조합이 올바르게 생성된다")
        fun `should generate correct option combinations`() {
            // Given
            val product = createTestProduct()
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L, "BLUE" to 0L),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf("S" to 0L, "M" to 0L),
            )

            product.optionGroups.addAll(listOf(colorGroup, sizeGroup))
            colorGroup.product = product
            sizeGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(4)

            // SKU ID에 옵션 조합이 포함되어 있는지 확인
            val skuIds = product.skus.map { it.skuId }
            assertThat(skuIds).anyMatch { it.contains("RED") && it.contains("S") }
            assertThat(skuIds).anyMatch { it.contains("RED") && it.contains("M") }
            assertThat(skuIds).anyMatch { it.contains("BLUE") && it.contains("S") }
            assertThat(skuIds).anyMatch { it.contains("BLUE") && it.contains("M") }
        }

        @Test
        @DisplayName("옵션 그룹의 ordering에 따라 정렬하여 SKU를 생성한다")
        fun `should generate SKUs ordered by option group ordering`() {
            // Given
            val product = createTestProduct()
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1, // 두 번째
                options = listOf("S" to 0L),
            )
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0, // 첫 번째
                options = listOf("RED" to 0L),
            )

            // ordering 역순으로 추가
            product.optionGroups.addAll(listOf(sizeGroup, colorGroup))
            colorGroup.product = product
            sizeGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(1)
            // SKU ID는 옵션 이름(키)으로 정렬되므로 "COLOR" -> "SIZE" 순서
            val skuId = product.skus.first().skuId
            assertThat(skuId).matches(".*RED-S.*")
        }

        @Test
        @DisplayName("옵션의 ordering에 따라 정렬하여 SKU를 생성한다")
        fun `should generate SKUs ordered by option ordering`() {
            // Given
            val product = createTestProduct()
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "BLACK" to 0L, // ordering = 0
                    "WHITE" to 0L, // ordering = 1
                    "RED" to 0L, // ordering = 2
                ),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(3)
            val skuIds = product.skus.map { it.skuId }
            // ordering 순서대로 생성됨을 확인
            assertThat(skuIds[0]).contains("BLACK")
            assertThat(skuIds[1]).contains("WHITE")
            assertThat(skuIds[2]).contains("RED")
        }
    }

    @Nested
    @DisplayName("가격 계산은")
    inner class PriceCalculationTest {

        @Test
        @DisplayName("추가 가격이 없을 때 기본 가격으로 SKU를 생성한다")
        fun `should use base price when no additional price`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "RED" to 0L,
                    "BLUE" to 0L,
                ),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(2)
            assertThat(product.skus.map { it.price }).allMatch { it == 10000L }
        }

        @Test
        @DisplayName("단일 옵션의 추가 가격을 계산한다")
        fun `should calculate price with single option additional price`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "RED" to 0L,
                    "BLUE" to 1000L,
                    "BLACK" to 2000L,
                ),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(3)
            val prices = product.skus.map { it.price }
            assertThat(prices).containsExactlyInAnyOrder(10000L, 11000L, 12000L)
        }

        @Test
        @DisplayName("여러 옵션의 추가 가격을 합산하여 계산한다")
        fun `should sum additional prices from multiple options`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf(
                    "RED" to 0L,
                    "BLUE" to 1000L,
                ),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf(
                    "S" to 0L,
                    "M" to 1000L,
                    "L" to 2000L,
                ),
            )

            product.optionGroups.addAll(listOf(colorGroup, sizeGroup))
            colorGroup.product = product
            sizeGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(6)
            val prices = product.skus.map { it.price }
            // RED(+0) + S(+0) = 10000
            // RED(+0) + M(+1000) = 11000
            // RED(+0) + L(+2000) = 12000
            // BLUE(+1000) + S(+0) = 11000
            // BLUE(+1000) + M(+1000) = 12000
            // BLUE(+1000) + L(+2000) = 13000
            assertThat(prices).containsExactlyInAnyOrder(
                10000L,
                11000L,
                11000L,
                12000L,
                12000L,
                13000L,
            )
        }

        @Test
        @DisplayName("세 개 옵션의 추가 가격을 합산하여 계산한다")
        fun `should sum additional prices from three options`() {
            // Given
            val product = createTestProduct(price = 10000)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 1000L),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf("M" to 2000L),
            )
            val materialGroup = createOptionGroup(
                name = "재질",
                ordering = 2,
                options = listOf("면" to 3000L),
            )

            product.optionGroups.addAll(listOf(colorGroup, sizeGroup, materialGroup))
            colorGroup.product = product
            sizeGroup.product = product
            materialGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(1)
            // 10000 + 1000 + 2000 + 3000 = 16000
            assertThat(product.skus.first().price).isEqualTo(16000L)
        }
    }

    @Nested
    @DisplayName("SKU ID 생성은")
    inner class SkuIdGenerationTest {

        @Test
        @DisplayName("각 SKU는 고유한 ID를 가진다")
        fun `should generate unique SKU IDs`() {
            // Given
            val product = createTestProduct()
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L, "BLUE" to 0L),
            )
            val sizeGroup = createOptionGroup(
                name = "SIZE",
                ordering = 1,
                options = listOf("S" to 0L, "M" to 0L),
            )

            product.optionGroups.addAll(listOf(colorGroup, sizeGroup))
            colorGroup.product = product
            sizeGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            val skuIds = product.skus.map { it.skuId }
            assertThat(skuIds).hasSize(4)
            assertThat(skuIds).doesNotHaveDuplicates()
        }

        @Test
        @DisplayName("SKU ID에 productCode가 포함된다")
        fun `should include productCode in SKU ID`() {
            // Given
            val productCode = "ELEC-20250108-A1B2"
            val product = createTestProduct(productCode = productCode)
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(1)
            assertThat(product.skus.first().skuId).startsWith(productCode)
        }
    }

    @Nested
    @DisplayName("Product와의 관계는")
    inner class ProductRelationshipTest {

        @Test
        @DisplayName("생성된 SKU가 Product에 추가된다")
        fun `should add generated SKUs to product`() {
            // Given
            val product = createTestProduct()
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L, "BLUE" to 0L),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            assertThat(product.skus).isEmpty()
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).isNotEmpty
            assertThat(product.skus).hasSize(2)
        }

        @Test
        @DisplayName("생성된 SKU가 Product를 참조한다")
        fun `should reference product in generated SKUs`() {
            // Given
            val product = createTestProduct()
            val colorGroup = createOptionGroup(
                name = "COLOR",
                ordering = 0,
                options = listOf("RED" to 0L),
            )

            product.optionGroups.add(colorGroup)
            colorGroup.product = product

            // When
            skuGenerator.generateSkus(product)

            // Then
            assertThat(product.skus).hasSize(1)
            assertThat(product.skus.first().product).isEqualTo(product)
        }
    }
}
