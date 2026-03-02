package com.koosco.catalogservice.domain.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProductStatus 상태 전이 테스트")
class ProductStatusTest {

    @Nested
    @DisplayName("canTransitionTo 메서드는")
    inner class CanTransitionToTest {

        @Test
        fun `DRAFT에서 ACTIVE로 전환 가능하다`() {
            assertThat(ProductStatus.DRAFT.canTransitionTo(ProductStatus.ACTIVE)).isTrue()
        }

        @Test
        fun `DRAFT에서 SUSPENDED로 전환 불가능하다`() {
            assertThat(ProductStatus.DRAFT.canTransitionTo(ProductStatus.SUSPENDED)).isFalse()
        }

        @Test
        fun `DRAFT에서 OUT_OF_STOCK으로 전환 불가능하다`() {
            assertThat(ProductStatus.DRAFT.canTransitionTo(ProductStatus.OUT_OF_STOCK)).isFalse()
        }

        @Test
        fun `DRAFT에서 DELETED로 전환 불가능하다`() {
            assertThat(ProductStatus.DRAFT.canTransitionTo(ProductStatus.DELETED)).isFalse()
        }

        @Test
        fun `ACTIVE에서 SUSPENDED로 전환 가능하다`() {
            assertThat(ProductStatus.ACTIVE.canTransitionTo(ProductStatus.SUSPENDED)).isTrue()
        }

        @Test
        fun `ACTIVE에서 OUT_OF_STOCK으로 전환 가능하다`() {
            assertThat(ProductStatus.ACTIVE.canTransitionTo(ProductStatus.OUT_OF_STOCK)).isTrue()
        }

        @Test
        fun `ACTIVE에서 DELETED로 전환 가능하다`() {
            assertThat(ProductStatus.ACTIVE.canTransitionTo(ProductStatus.DELETED)).isTrue()
        }

        @Test
        fun `ACTIVE에서 DRAFT로 전환 불가능하다`() {
            assertThat(ProductStatus.ACTIVE.canTransitionTo(ProductStatus.DRAFT)).isFalse()
        }

        @Test
        fun `SUSPENDED에서 ACTIVE로 전환 가능하다`() {
            assertThat(ProductStatus.SUSPENDED.canTransitionTo(ProductStatus.ACTIVE)).isTrue()
        }

        @Test
        fun `SUSPENDED에서 DELETED로 전환 가능하다`() {
            assertThat(ProductStatus.SUSPENDED.canTransitionTo(ProductStatus.DELETED)).isTrue()
        }

        @Test
        fun `SUSPENDED에서 OUT_OF_STOCK으로 전환 불가능하다`() {
            assertThat(ProductStatus.SUSPENDED.canTransitionTo(ProductStatus.OUT_OF_STOCK)).isFalse()
        }

        @Test
        fun `OUT_OF_STOCK에서 ACTIVE로 전환 가능하다`() {
            assertThat(ProductStatus.OUT_OF_STOCK.canTransitionTo(ProductStatus.ACTIVE)).isTrue()
        }

        @Test
        fun `OUT_OF_STOCK에서 DELETED로 전환 가능하다`() {
            assertThat(ProductStatus.OUT_OF_STOCK.canTransitionTo(ProductStatus.DELETED)).isTrue()
        }

        @Test
        fun `OUT_OF_STOCK에서 SUSPENDED로 전환 불가능하다`() {
            assertThat(ProductStatus.OUT_OF_STOCK.canTransitionTo(ProductStatus.SUSPENDED)).isFalse()
        }

        @Test
        fun `DELETED에서 어떤 상태로도 전환 불가능하다`() {
            ProductStatus.entries.forEach { target ->
                assertThat(ProductStatus.DELETED.canTransitionTo(target)).isFalse()
            }
        }
    }
}
