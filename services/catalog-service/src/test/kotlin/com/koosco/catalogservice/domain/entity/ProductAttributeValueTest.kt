package com.koosco.catalogservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductAttributeValue 테스트")
class ProductAttributeValueTest {

    @Nested
    @DisplayName("create는")
    inner class CreateTest {

        @Test
        fun `속성값을 생성한다`() {
            val attrValue = ProductAttributeValue.create(
                productId = 1L,
                attributeId = 2L,
                value = "빨강",
            )

            assertThat(attrValue.productId).isEqualTo(1L)
            assertThat(attrValue.attributeId).isEqualTo(2L)
            assertThat(attrValue.value).isEqualTo("빨강")
            assertThat(attrValue.createdAt).isNotNull()
            assertThat(attrValue.updatedAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("updateValue는")
    inner class UpdateValueTest {

        @Test
        fun `값을 변경한다`() {
            val attrValue = ProductAttributeValue.create(1L, 2L, "빨강")

            attrValue.updateValue("파랑")

            assertThat(attrValue.value).isEqualTo("파랑")
        }
    }

    @Nested
    @DisplayName("preUpdate는")
    inner class PreUpdateTest {

        @Test
        fun `updatedAt을 갱신한다`() {
            val attrValue = ProductAttributeValue.create(1L, 2L, "빨강")
            val before = attrValue.updatedAt

            Thread.sleep(10)
            attrValue.preUpdate()

            assertThat(attrValue.updatedAt).isAfter(before)
        }
    }
}
