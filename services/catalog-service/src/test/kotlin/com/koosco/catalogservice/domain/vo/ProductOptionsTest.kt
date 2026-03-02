package com.koosco.catalogservice.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductOptions 값 객체 테스트")
class ProductOptionsTest {

    @Nested
    @DisplayName("from 메서드는")
    inner class FromTest {

        @Test
        fun `Map으로부터 ProductOptions를 생성한다`() {
            val options = ProductOptions.from(mapOf("색상" to "빨강", "사이즈" to "M"))

            assertThat(options.asMap()).hasSize(2)
            assertThat(options.asMap()["색상"]).isEqualTo("빨강")
        }
    }

    @Nested
    @DisplayName("fromJson 메서드는")
    inner class FromJsonTest {

        @Test
        fun `JSON 문자열로부터 ProductOptions를 생성한다`() {
            val options = ProductOptions.fromJson("""{"색상":"빨강","사이즈":"M"}""")

            assertThat(options.asMap()).hasSize(2)
            assertThat(options.asMap()["색상"]).isEqualTo("빨강")
        }
    }

    @Nested
    @DisplayName("equals 메서드는")
    inner class EqualsTest {

        @Test
        fun `순서와 관계없이 동일한 옵션이면 같다`() {
            val options1 = ProductOptions.from(mapOf("색상" to "빨강", "사이즈" to "M"))
            val options2 = ProductOptions.from(mapOf("사이즈" to "M", "색상" to "빨강"))

            assertThat(options1).isEqualTo(options2)
        }

        @Test
        fun `다른 옵션이면 다르다`() {
            val options1 = ProductOptions.from(mapOf("색상" to "빨강"))
            val options2 = ProductOptions.from(mapOf("색상" to "파랑"))

            assertThat(options1).isNotEqualTo(options2)
        }
    }

    @Nested
    @DisplayName("toJson 메서드는")
    inner class ToJsonTest {

        @Test
        fun `정렬된 JSON 문자열을 반환한다`() {
            val options = ProductOptions.from(mapOf("Z" to "z", "A" to "a"))

            val json = options.toJson()

            assertThat(json).isEqualTo("""{"A":"a","Z":"z"}""")
        }
    }

    @Nested
    @DisplayName("isEmpty 메서드는")
    inner class IsEmptyTest {

        @Test
        fun `빈 옵션이면 true를 반환한다`() {
            val options = ProductOptions.from(emptyMap())

            assertThat(options.isEmpty()).isTrue()
        }

        @Test
        fun `옵션이 있으면 false를 반환한다`() {
            val options = ProductOptions.from(mapOf("색상" to "빨강"))

            assertThat(options.isEmpty()).isFalse()
        }
    }

    @Nested
    @DisplayName("hashCode 메서드는")
    inner class HashCodeTest {

        @Test
        fun `동일한 옵션은 동일한 해시코드를 가진다`() {
            val options1 = ProductOptions.from(mapOf("색상" to "빨강"))
            val options2 = ProductOptions.from(mapOf("색상" to "빨강"))

            assertThat(options1.hashCode()).isEqualTo(options2.hashCode())
        }
    }

    @Nested
    @DisplayName("toString 메서드는")
    inner class ToStringTest {

        @Test
        fun `정규화된 문자열을 반환한다`() {
            val options = ProductOptions.from(mapOf("색상" to "빨강"))

            assertThat(options.toString()).contains("색상")
            assertThat(options.toString()).contains("빨강")
        }
    }
}
