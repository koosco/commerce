package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.vo.CreateOptionSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductOptionGroup 테스트")
class ProductOptionGroupTest {

    @Nested
    @DisplayName("create는")
    inner class CreateTest {

        @Test
        fun `옵션 그룹과 옵션을 함께 생성한다`() {
            val specs = listOf(
                CreateOptionSpec("빨강", 0, 0),
                CreateOptionSpec("파랑", 1000, 1),
            )

            val group = ProductOptionGroup.create("색상", 0, specs)

            assertThat(group.name).isEqualTo("색상")
            assertThat(group.ordering).isEqualTo(0)
            assertThat(group.options).hasSize(2)
            assertThat(group.options[0].name).isEqualTo("빨강")
            assertThat(group.options[0].additionalPrice).isEqualTo(0)
            assertThat(group.options[1].name).isEqualTo("파랑")
            assertThat(group.options[1].additionalPrice).isEqualTo(1000)
        }

        @Test
        fun `생성된 옵션의 optionGroup 참조가 설정된다`() {
            val specs = listOf(CreateOptionSpec("빨강", 0, 0))

            val group = ProductOptionGroup.create("색상", 0, specs)

            assertThat(group.options.first().optionGroup).isEqualTo(group)
        }

        @Test
        fun `빈 옵션 목록으로 생성 가능하다`() {
            val group = ProductOptionGroup.create("색상", 0, emptyList())

            assertThat(group.options).isEmpty()
        }
    }

    @Nested
    @DisplayName("addOption은")
    inner class AddOptionTest {

        @Test
        fun `옵션을 추가하고 양방향 관계를 설정한다`() {
            val group = ProductOptionGroup(name = "색상", ordering = 0)
            val option = ProductOption(name = "빨강", additionalPrice = 0, ordering = 0)

            group.addOption(option)

            assertThat(group.options).hasSize(1)
            assertThat(option.optionGroup).isEqualTo(group)
        }
    }

    @Nested
    @DisplayName("preUpdate는")
    inner class PreUpdateTest {

        @Test
        fun `updatedAt을 갱신한다`() {
            val group = ProductOptionGroup(name = "색상", ordering = 0)
            val before = group.updatedAt

            Thread.sleep(10)
            group.preUpdate()

            assertThat(group.updatedAt).isAfter(before)
        }
    }
}
