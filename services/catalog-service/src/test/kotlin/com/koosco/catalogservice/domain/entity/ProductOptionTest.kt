package com.koosco.catalogservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductOption 테스트")
class ProductOptionTest {

    @Nested
    @DisplayName("생성은")
    inner class CreateTest {

        @Test
        fun `기본값으로 생성된다`() {
            val option = ProductOption(name = "빨강", additionalPrice = 1000, ordering = 0)

            assertThat(option.name).isEqualTo("빨강")
            assertThat(option.additionalPrice).isEqualTo(1000)
            assertThat(option.ordering).isEqualTo(0)
            assertThat(option.createdAt).isNotNull()
            assertThat(option.updatedAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("preUpdate는")
    inner class PreUpdateTest {

        @Test
        fun `updatedAt을 갱신한다`() {
            val option = ProductOption(name = "빨강", additionalPrice = 0, ordering = 0)
            val before = option.updatedAt

            Thread.sleep(10)
            option.preUpdate()

            assertThat(option.updatedAt).isAfter(before)
        }
    }
}
