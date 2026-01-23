package com.koosco.catalogservice.product.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.koosco.catalogservice.product.domain.entity.Product
import com.koosco.catalogservice.product.domain.entity.ProductSku
import com.koosco.catalogservice.product.domain.enums.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductSku 도메인 테스트")
class ProductSkuTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Nested
    @DisplayName("generate 메서드는")
    inner class GenerateTest {

        @Test
        @DisplayName("옵션이 없을 때 productCode만으로 SKU ID를 생성한다")
        fun `should generate SKU ID with productCode only when no options`() {
            // Given
            val productCode = "ELEC-20250108-A1B2"
            val options = emptyMap<String, String>()

            // When
            val skuId = ProductSku.generate(productCode, options)

            // Then
            assertThat(skuId).isNotNull
            assertThat(skuId).startsWith(productCode)
            assertThat(skuId).contains("--") // productCode--hash 형태
        }

        @Test
        @DisplayName("단일 옵션으로 SKU ID를 생성한다")
        fun `should generate SKU ID with single option`() {
            // Given
            val productCode = "FASH-20250108-C3D4"
            val options = mapOf("색상" to "빨강")

            // When
            val skuId = ProductSku.generate(productCode, options)

            // Then
            assertThat(skuId).isNotNull
            assertThat(skuId).startsWith(productCode)
            assertThat(skuId).contains("빨강")
            assertThat(skuId).matches(".*-[0-9A-F]+$") // 해시값 포함
        }

        @Test
        @DisplayName("여러 옵션으로 SKU ID를 생성한다")
        fun `should generate SKU ID with multiple options`() {
            // Given
            val productCode = "FASH-20250108-E5F6"
            val options = mapOf(
                "색상" to "파랑",
                "사이즈" to "M",
            )

            // When
            val skuId = ProductSku.generate(productCode, options)

            // Then
            assertThat(skuId).isNotNull
            assertThat(skuId).startsWith(productCode)
            assertThat(skuId).contains("M-파랑") // 키 정렬 순서: 사이즈, 색상 -> 값: M, 파랑
            assertThat(skuId).matches(".*-[0-9A-F]+$")
        }

        @Test
        @DisplayName("옵션 순서와 관계없이 동일한 SKU ID를 생성한다")
        fun `should generate same SKU ID regardless of option order`() {
            // Given
            val productCode = "FOOD-20250108-G7H8"
            val options1 = mapOf("색상" to "빨강", "사이즈" to "L")
            val options2 = mapOf("사이즈" to "L", "색상" to "빨강")

            // When
            val skuId1 = ProductSku.generate(productCode, options1)
            val skuId2 = ProductSku.generate(productCode, options2)

            // Then
            assertThat(skuId1).isEqualTo(skuId2)
        }

        @Test
        @DisplayName("다른 옵션 조합은 다른 SKU ID를 생성한다")
        fun `should generate different SKU IDs for different option combinations`() {
            // Given
            val productCode = "PRD-20250108-I9J0"
            val options1 = mapOf("색상" to "빨강", "사이즈" to "S")
            val options2 = mapOf("색상" to "파랑", "사이즈" to "M")

            // When
            val skuId1 = ProductSku.generate(productCode, options1)
            val skuId2 = ProductSku.generate(productCode, options2)

            // Then
            assertThat(skuId1).isNotEqualTo(skuId2)
        }

        @Test
        @DisplayName("특수문자가 포함된 옵션값도 처리한다")
        fun `should handle option values with special characters`() {
            // Given
            val productCode = "ELEC-20250108-K1L2"
            val options = mapOf("저장용량" to "256GB", "색상" to "스페이스 그레이")

            // When
            val skuId = ProductSku.generate(productCode, options)

            // Then
            assertThat(skuId).isNotNull
            assertThat(skuId).contains("256GB")
            assertThat(skuId).contains("스페이스 그레이")
        }
    }

    @Nested
    @DisplayName("create 메서드는")
    inner class CreateTest {

        private fun createTestProduct(productCode: String = "TEST-20250108-X1Y2"): Product = Product(
            productCode = productCode,
            name = "테스트 상품",
            price = 10000,
            status = ProductStatus.ACTIVE,
        )

        @Test
        @DisplayName("옵션 없이 ProductSku를 생성한다")
        fun `should create ProductSku without options`() {
            // Given
            val product = createTestProduct()
            val options = emptyMap<String, String>()
            val price = 10000L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            assertThat(sku).isNotNull
            assertThat(sku.product).isEqualTo(product)
            assertThat(sku.price).isEqualTo(price)

            // JSON 검증
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            assertThat(parsedOptions).isEmpty()
            assertThat(sku.skuId).startsWith(product.productCode)
        }

        @Test
        @DisplayName("단일 옵션으로 ProductSku를 생성한다")
        fun `should create ProductSku with single option`() {
            // Given
            val product = createTestProduct()
            val options = mapOf("색상" to "빨강")
            val price = 10000L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            assertThat(sku).isNotNull
            assertThat(sku.product).isEqualTo(product)
            assertThat(sku.price).isEqualTo(price)

            // JSON 검증
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            assertThat(parsedOptions).hasSize(1)
            assertThat(parsedOptions["색상"]).isEqualTo("빨강")
            assertThat(sku.skuId).contains("빨강")
        }

        @Test
        @DisplayName("여러 옵션으로 ProductSku를 생성한다")
        fun `should create ProductSku with multiple options`() {
            // Given
            val product = createTestProduct()
            val options = mapOf(
                "색상" to "파랑",
                "사이즈" to "M",
                "재질" to "면",
            )
            val price = 15000L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            assertThat(sku).isNotNull
            assertThat(sku.product).isEqualTo(product)
            assertThat(sku.price).isEqualTo(price)

            // JSON 검증
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            assertThat(parsedOptions).hasSize(3)
            assertThat(parsedOptions["재질"]).isEqualTo("면")
            assertThat(parsedOptions["사이즈"]).isEqualTo("M")
            assertThat(parsedOptions["색상"]).isEqualTo("파랑")
        }

        @Test
        @DisplayName("옵션값이 정렬되어 저장된다")
        fun `should store option values in sorted order`() {
            // Given
            val product = createTestProduct()
            val options = mapOf(
                "색상" to "검정",
                "사이즈" to "XL",
                "브랜드" to "Nike",
            )
            val price = 20000L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            // JSON에서 키가 정렬된 순서로 저장되는지 확인
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            val keys = parsedOptions.keys.toList()
            assertThat(keys).containsExactly("브랜드", "사이즈", "색상") // 정렬된 순서
            assertThat(parsedOptions["브랜드"]).isEqualTo("Nike")
            assertThat(parsedOptions["사이즈"]).isEqualTo("XL")
            assertThat(parsedOptions["색상"]).isEqualTo("검정")
        }

        @Test
        @DisplayName("가격이 0원인 경우도 생성 가능하다")
        fun `should create ProductSku with zero price`() {
            // Given
            val product = createTestProduct()
            val options = mapOf("타입" to "무료샘플")
            val price = 0L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            assertThat(sku).isNotNull
            assertThat(sku.price).isEqualTo(0L)
        }

        @Test
        @DisplayName("동일한 옵션으로 생성한 SKU는 동일한 SKU ID를 갖는다")
        fun `should have same SKU ID for same options`() {
            // Given
            val product = createTestProduct()
            val options = mapOf("색상" to "빨강", "사이즈" to "L")
            val price = 12000L

            // When
            val sku1 = ProductSku.create(product, options, price)
            val sku2 = ProductSku.create(product, options, price)

            // Then
            assertThat(sku1.skuId).isEqualTo(sku2.skuId)
        }

        @Test
        @DisplayName("다른 옵션으로 생성한 SKU는 다른 SKU ID를 갖는다")
        fun `should have different SKU ID for different options`() {
            // Given
            val product = createTestProduct()
            val options1 = mapOf("색상" to "빨강")
            val options2 = mapOf("색상" to "파랑")
            val price = 10000L

            // When
            val sku1 = ProductSku.create(product, options1, price)
            val sku2 = ProductSku.create(product, options2, price)

            // Then
            assertThat(sku1.skuId).isNotEqualTo(sku2.skuId)
        }

        @Test
        @DisplayName("생성 시간이 자동으로 설정된다")
        fun `should set creation time automatically`() {
            // Given
            val product = createTestProduct()
            val options = mapOf("색상" to "빨강")
            val price = 10000L

            // When
            val sku = ProductSku.create(product, options, price)

            // Then
            assertThat(sku.createdAt).isNotNull
        }
    }

    @Nested
    @DisplayName("옵션값 JSON 포맷은")
    inner class OptionValuesFormatTest {

        @Test
        @DisplayName("유효한 JSON 형태로 저장된다")
        fun `should format as valid JSON`() {
            // Given
            val product = Product(
                productCode = "TEST-CODE",
                name = "테스트",
                price = 10000,
                status = ProductStatus.ACTIVE,
            )
            val options = mapOf("A" to "a", "B" to "b", "C" to "c")

            // When
            val sku = ProductSku.create(product, options, 10000)

            // Then
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            assertThat(parsedOptions).isEqualTo(options)
        }

        @Test
        @DisplayName("키가 알파벳 순으로 정렬되어 JSON으로 저장된다")
        fun `should sort keys alphabetically in JSON`() {
            // Given
            val product = Product(
                productCode = "TEST-CODE",
                name = "테스트",
                price = 10000,
                status = ProductStatus.ACTIVE,
            )
            val options = mapOf("Z" to "z", "A" to "a", "M" to "m")

            // When
            val sku = ProductSku.create(product, options, 10000)

            // Then
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            val keys = parsedOptions.keys.toList()
            assertThat(keys).containsExactly("A", "M", "Z")
            assertThat(parsedOptions["A"]).isEqualTo("a")
            assertThat(parsedOptions["M"]).isEqualTo("m")
            assertThat(parsedOptions["Z"]).isEqualTo("z")
        }

        @Test
        @DisplayName("빈 옵션은 빈 JSON 객체로 저장된다")
        fun `should store empty options as empty JSON object`() {
            // Given
            val product = Product(
                productCode = "TEST-CODE",
                name = "테스트",
                price = 10000,
                status = ProductStatus.ACTIVE,
            )
            val options = emptyMap<String, String>()

            // When
            val sku = ProductSku.create(product, options, 10000)

            // Then
            assertThat(sku.optionValues).isEqualTo("{}")
            val parsedOptions: Map<String, String> = objectMapper.readValue(sku.optionValues)
            assertThat(parsedOptions).isEmpty()
        }
    }
}
