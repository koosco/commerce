package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.AttributeType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CategoryAttribute 도메인 테스트")
class CategoryAttributeTest {

    @Nested
    @DisplayName("create 메서드는")
    inner class CreateTest {

        @Test
        fun `STRING 타입 속성을 생성한다`() {
            val attr = CategoryAttribute.create(
                categoryId = 1L,
                name = "소재",
                type = AttributeType.STRING,
                required = false,
                options = null,
                ordering = 0,
            )

            assertThat(attr.name).isEqualTo("소재")
            assertThat(attr.type).isEqualTo(AttributeType.STRING)
        }

        @Test
        fun `ENUM 타입에 options가 없으면 예외를 던진다`() {
            assertThatThrownBy {
                CategoryAttribute.create(
                    categoryId = 1L,
                    name = "색상",
                    type = AttributeType.ENUM,
                    required = true,
                    options = null,
                    ordering = 0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ENUM 타입에 빈 options가 있으면 예외를 던진다`() {
            assertThatThrownBy {
                CategoryAttribute.create(
                    categoryId = 1L,
                    name = "색상",
                    type = AttributeType.ENUM,
                    required = true,
                    options = "",
                    ordering = 0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ENUM 타입에 options가 있으면 정상 생성된다`() {
            val attr = CategoryAttribute.create(
                categoryId = 1L,
                name = "색상",
                type = AttributeType.ENUM,
                required = true,
                options = "빨강,파랑,초록",
                ordering = 0,
            )

            assertThat(attr.options).isEqualTo("빨강,파랑,초록")
        }
    }

    @Nested
    @DisplayName("validateValue 메서드는")
    inner class ValidateValueTest {

        @Test
        fun `NUMBER 타입에 숫자가 아니면 예외를 던진다`() {
            val attr = CategoryAttribute.create(1L, "무게", AttributeType.NUMBER, false, null, 0)

            assertThatThrownBy { attr.validateValue("abc") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `NUMBER 타입에 숫자면 통과한다`() {
            val attr = CategoryAttribute.create(1L, "무게", AttributeType.NUMBER, false, null, 0)

            attr.validateValue("3.14")
        }

        @Test
        fun `BOOLEAN 타입에 true_false가 아니면 예외를 던진다`() {
            val attr = CategoryAttribute.create(1L, "방수", AttributeType.BOOLEAN, false, null, 0)

            assertThatThrownBy { attr.validateValue("yes") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `BOOLEAN 타입에 true면 통과한다`() {
            val attr = CategoryAttribute.create(1L, "방수", AttributeType.BOOLEAN, false, null, 0)

            attr.validateValue("true")
        }

        @Test
        fun `ENUM 타입에 허용되지 않은 값이면 예외를 던진다`() {
            val attr = CategoryAttribute.create(1L, "색상", AttributeType.ENUM, false, "빨강,파랑", 0)

            assertThatThrownBy { attr.validateValue("초록") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ENUM 타입에 허용된 값이면 통과한다`() {
            val attr = CategoryAttribute.create(1L, "색상", AttributeType.ENUM, false, "빨강,파랑", 0)

            attr.validateValue("빨강")
        }

        @Test
        fun `STRING 타입은 모든 값이 통과한다`() {
            val attr = CategoryAttribute.create(1L, "설명", AttributeType.STRING, false, null, 0)

            attr.validateValue("아무 값이나")
        }
    }

    @Nested
    @DisplayName("getOptionList 메서드는")
    inner class GetOptionListTest {

        @Test
        fun `options가 null이면 빈 리스트를 반환한다`() {
            val attr = CategoryAttribute.create(1L, "소재", AttributeType.STRING, false, null, 0)

            assertThat(attr.getOptionList()).isEmpty()
        }

        @Test
        fun `options를 파싱하여 리스트를 반환한다`() {
            val attr = CategoryAttribute.create(1L, "색상", AttributeType.ENUM, false, "빨강, 파랑, 초록", 0)

            assertThat(attr.getOptionList()).containsExactly("빨강", "파랑", "초록")
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    inner class UpdateTest {

        @Test
        fun `필드를 갱신한다`() {
            val attr = CategoryAttribute.create(1L, "소재", AttributeType.STRING, false, null, 0)

            attr.update(name = "재질", required = true, options = null, ordering = 5)

            assertThat(attr.name).isEqualTo("재질")
            assertThat(attr.required).isTrue()
            assertThat(attr.ordering).isEqualTo(5)
        }

        @Test
        fun `null이면 변경하지 않는다`() {
            val attr = CategoryAttribute.create(1L, "소재", AttributeType.STRING, false, null, 0)

            attr.update(name = null, required = null, options = null, ordering = null)

            assertThat(attr.name).isEqualTo("소재")
        }
    }
}
