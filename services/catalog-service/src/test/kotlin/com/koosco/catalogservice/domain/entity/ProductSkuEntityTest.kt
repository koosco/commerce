package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.SkuStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductSku 도메인 상태 테스트")
class ProductSkuEntityTest {

    private val product = Product(
        productCode = "TEST-001",
        name = "테스트",
        price = 10000,
        status = ProductStatus.ACTIVE,
    )

    @Nested
    @DisplayName("deactivate 메서드는")
    inner class DeactivateTest {

        @Test
        fun `상태를 DEACTIVATED로 변경한다`() {
            val sku = ProductSku(
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = "{}",
                status = SkuStatus.ACTIVE,
            )

            sku.deactivate()

            assertThat(sku.status).isEqualTo(SkuStatus.DEACTIVATED)
        }
    }

    @Nested
    @DisplayName("isActive 메서드는")
    inner class IsActiveTest {

        @Test
        fun `ACTIVE 상태이면 true를 반환한다`() {
            val sku = ProductSku(
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = "{}",
                status = SkuStatus.ACTIVE,
            )

            assertThat(sku.isActive()).isTrue()
        }

        @Test
        fun `DEACTIVATED 상태이면 false를 반환한다`() {
            val sku = ProductSku(
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = "{}",
                status = SkuStatus.DEACTIVATED,
            )

            assertThat(sku.isActive()).isFalse()
        }
    }
}
