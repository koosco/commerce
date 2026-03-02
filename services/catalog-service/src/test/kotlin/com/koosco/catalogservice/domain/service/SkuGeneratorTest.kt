package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.enums.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SkuGenerator 테스트")
class SkuGeneratorTest {

    private val skuGenerator = SkuGenerator()

    private fun createProduct(price: Long = 10000): Product = Product(
        productCode = "TEST-001",
        name = "테스트 상품",
        price = price,
        status = ProductStatus.ACTIVE,
    )

    private fun createOptionGroup(name: String, ordering: Int, options: List<Pair<String, Long>>): ProductOptionGroup {
        val group = ProductOptionGroup(name = name, ordering = ordering)
        options.forEachIndexed { idx, (optName, additionalPrice) ->
            val option = ProductOption(name = optName, additionalPrice = additionalPrice, ordering = idx)
            group.addOption(option)
        }
        return group
    }

    @Nested
    @DisplayName("generateSkus는")
    inner class GenerateSkusTest {

        @Test
        fun `옵션 그룹이 없으면 SKU를 생성하지 않는다`() {
            val product = createProduct()

            skuGenerator.generateSkus(product)

            assertThat(product.skus).isEmpty()
        }

        @Test
        fun `단일 옵션 그룹으로 SKU를 생성한다`() {
            val product = createProduct()
            val group = createOptionGroup("색상", 0, listOf("빨강" to 0L, "파랑" to 0L))
            product.optionGroups.add(group)
            group.product = product

            skuGenerator.generateSkus(product)

            assertThat(product.skus).hasSize(2)
        }

        @Test
        fun `두 옵션 그룹의 데카르트 곱으로 SKU를 생성한다`() {
            val product = createProduct()
            val colorGroup = createOptionGroup("색상", 0, listOf("빨강" to 0L, "파랑" to 0L))
            val sizeGroup = createOptionGroup("사이즈", 1, listOf("S" to 0L, "M" to 1000L))
            product.optionGroups.addAll(listOf(colorGroup, sizeGroup))
            colorGroup.product = product
            sizeGroup.product = product

            skuGenerator.generateSkus(product)

            assertThat(product.skus).hasSize(4)
        }

        @Test
        fun `추가 가격이 SKU 가격에 반영된다`() {
            val product = createProduct(price = 10000)
            val group = createOptionGroup("사이즈", 0, listOf("S" to 0L, "L" to 2000L))
            product.optionGroups.add(group)
            group.product = product

            skuGenerator.generateSkus(product)

            val prices = product.skus.map { it.price }.sorted()
            assertThat(prices).containsExactly(10000L, 12000L)
        }

        @Test
        fun `여러 옵션 그룹의 추가 가격을 합산한다`() {
            val product = createProduct(price = 10000)
            val colorGroup = createOptionGroup("색상", 0, listOf("빨강" to 1000L))
            val sizeGroup = createOptionGroup("사이즈", 1, listOf("M" to 2000L))
            product.optionGroups.addAll(listOf(colorGroup, sizeGroup))
            colorGroup.product = product
            sizeGroup.product = product

            skuGenerator.generateSkus(product)

            assertThat(product.skus).hasSize(1)
            assertThat(product.skus.first().price).isEqualTo(13000L)
        }
    }
}
