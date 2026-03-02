package com.koosco.catalogservice.application.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DiscountPolicy Command 테스트")
class DiscountPolicyCommandTest {

    @Nested
    @DisplayName("UpdateDiscountPolicyCommand는")
    inner class UpdateDiscountPolicyCommandTest {

        @Test
        fun `모든 속성을 갖는다`() {
            val command = UpdateDiscountPolicyCommand(
                productId = 1L,
                discountPolicyId = 2L,
                name = "수정된 할인",
            )

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.discountPolicyId).isEqualTo(2L)
            assertThat(command.name).isEqualTo("수정된 할인")
        }

        @Test
        fun `name이 null일 수 있다`() {
            val command = UpdateDiscountPolicyCommand(
                productId = 1L,
                discountPolicyId = 2L,
                name = null,
            )

            assertThat(command.name).isNull()
        }
    }

    @Nested
    @DisplayName("DeleteDiscountPolicyCommand는")
    inner class DeleteDiscountPolicyCommandTest {

        @Test
        fun `모든 속성을 갖는다`() {
            val command = DeleteDiscountPolicyCommand(productId = 1L, discountPolicyId = 2L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.discountPolicyId).isEqualTo(2L)
        }
    }

    @Nested
    @DisplayName("GetDiscountPoliciesCommand는")
    inner class GetDiscountPoliciesCommandTest {

        @Test
        fun `productId를 갖는다`() {
            val command = GetDiscountPoliciesCommand(productId = 1L)

            assertThat(command.productId).isEqualTo(1L)
        }
    }
}
