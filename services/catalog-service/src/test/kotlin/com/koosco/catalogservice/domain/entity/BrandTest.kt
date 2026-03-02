package com.koosco.catalogservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Brand 도메인 테스트")
class BrandTest {

    @Nested
    @DisplayName("update 메서드는")
    inner class UpdateTest {

        @Test
        fun `이름을 변경한다`() {
            val brand = Brand(id = 1L, name = "Nike")

            brand.update(name = "Adidas", logoImageUrl = null)

            assertThat(brand.name).isEqualTo("Adidas")
        }

        @Test
        fun `로고 이미지 URL을 변경한다`() {
            val brand = Brand(id = 1L, name = "Nike")

            brand.update(name = null, logoImageUrl = "http://logo.jpg")

            assertThat(brand.logoImageUrl).isEqualTo("http://logo.jpg")
        }

        @Test
        fun `null이면 변경하지 않는다`() {
            val brand = Brand(id = 1L, name = "Nike", logoImageUrl = "http://old.jpg")

            brand.update(name = null, logoImageUrl = null)

            assertThat(brand.name).isEqualTo("Nike")
            assertThat(brand.logoImageUrl).isEqualTo("http://old.jpg")
        }
    }

    @Nested
    @DisplayName("softDelete 메서드는")
    inner class SoftDeleteTest {

        @Test
        fun `deletedAt을 설정한다`() {
            val brand = Brand(id = 1L, name = "Nike")

            brand.softDelete()

            assertThat(brand.deletedAt).isNotNull()
        }
    }
}
