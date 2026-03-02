package com.koosco.catalogservice.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CategoryCodeGenerator 테스트")
class CategoryCodeGeneratorTest {

    @Nested
    @DisplayName("generate 메서드는")
    inner class GenerateTest {

        @Test
        fun `이름을 대문자로 변환하고 랜덤 접미사를 붙인다`() {
            val code = CategoryCodeGenerator.generate("electronics")

            assertThat(code).startsWith("ELECTRONICS_")
            assertThat(code).hasSize("ELECTRONICS_".length + 4)
        }

        @Test
        fun `공백을 언더스코어로 변환한다`() {
            val code = CategoryCodeGenerator.generate("MEN TOPS")

            assertThat(code).startsWith("MEN_TOPS_")
        }

        @Test
        fun `매번 다른 코드를 생성한다`() {
            val code1 = CategoryCodeGenerator.generate("test")
            val code2 = CategoryCodeGenerator.generate("test")

            assertThat(code1).isNotEqualTo(code2)
        }
    }
}
