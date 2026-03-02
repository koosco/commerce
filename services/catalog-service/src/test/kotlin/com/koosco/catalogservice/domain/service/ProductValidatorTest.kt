package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.vo.CreateOptionSpec
import com.koosco.catalogservice.domain.vo.OptionGroupCreateSpec
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductValidator 서비스 테스트")
class ProductValidatorTest {

    private val validator = ProductValidator()

    @Nested
    @DisplayName("validateSkuCount 메서드는")
    inner class ValidateSkuCountTest {

        @Test
        fun `빈 옵션 그룹이면 통과한다`() {
            validator.validateSkuCount(emptyList())
        }

        @Test
        fun `옵션 그룹이 5개를 초과하면 예외를 던진다`() {
            val specs = (1..6).map { i ->
                OptionGroupCreateSpec(
                    name = "그룹$i",
                    ordering = i,
                    options = listOf(CreateOptionSpec("옵션1", 0, 0)),
                )
            }

            assertThatThrownBy { validator.validateSkuCount(specs) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("옵션 그룹은 최대")
        }

        @Test
        fun `그룹 내 옵션이 20개를 초과하면 예외를 던진다`() {
            val options = (1..21).map { CreateOptionSpec("옵션$it", 0, it) }
            val specs = listOf(OptionGroupCreateSpec("색상", 0, options))

            assertThatThrownBy { validator.validateSkuCount(specs) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("최대")
        }

        @Test
        fun `예상 SKU 수가 500을 초과하면 예외를 던진다`() {
            // 3그룹 x 10옵션 = 1000 SKUs
            val specs = (1..3).map { i ->
                val options = (1..10).map { CreateOptionSpec("옵션$it", 0, it) }
                OptionGroupCreateSpec("그룹$i", i, options)
            }

            assertThatThrownBy { validator.validateSkuCount(specs) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("제한을 초과")
        }

        @Test
        fun `정상 범위의 옵션이면 통과한다`() {
            val specs = listOf(
                OptionGroupCreateSpec(
                    "색상",
                    0,
                    listOf(CreateOptionSpec("빨강", 0, 0), CreateOptionSpec("파랑", 0, 1)),
                ),
                OptionGroupCreateSpec(
                    "사이즈",
                    1,
                    listOf(CreateOptionSpec("S", 0, 0), CreateOptionSpec("M", 0, 1)),
                ),
            )

            validator.validateSkuCount(specs) // 예외 없이 통과
        }
    }

    @Nested
    @DisplayName("validateOptionGroupStructure 메서드는")
    inner class ValidateOptionGroupStructureTest {

        @Test
        fun `정상 이름이면 경고 없이 통과한다`() {
            val specs = listOf(
                OptionGroupCreateSpec(
                    "색상",
                    0,
                    listOf(CreateOptionSpec("빨강", 0, 0)),
                ),
            )

            validator.validateOptionGroupStructure(specs) // 경고 로그만 출력
        }

        @Test
        fun `의심스러운 이름이어도 예외를 던지지 않는다`() {
            val specs = listOf(
                OptionGroupCreateSpec(
                    "RED",
                    0,
                    listOf(CreateOptionSpec("옵션1", 0, 0)),
                ),
            )

            validator.validateOptionGroupStructure(specs) // 경고 로그만 출력, 예외 없음
        }
    }
}
