package com.koosco.catalogservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Like 엔티티 테스트")
class LikeEntityTest {

    @Nested
    @DisplayName("ProductLike는")
    inner class ProductLikeTest {

        @Test
        fun `생성 시 필드가 올바르게 설정된다`() {
            val like = ProductLike(productId = 1L, userId = 2L)

            assertThat(like.productId).isEqualTo(1L)
            assertThat(like.userId).isEqualTo(2L)
            assertThat(like.createdAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("ReviewLike는")
    inner class ReviewLikeTest {

        @Test
        fun `생성 시 필드가 올바르게 설정된다`() {
            val like = ReviewLike(reviewId = 1L, userId = 2L)

            assertThat(like.reviewId).isEqualTo(1L)
            assertThat(like.userId).isEqualTo(2L)
            assertThat(like.createdAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("SnapLike는")
    inner class SnapLikeTest {

        @Test
        fun `생성 시 필드가 올바르게 설정된다`() {
            val like = SnapLike(snapId = 1L, userId = 2L)

            assertThat(like.snapId).isEqualTo(1L)
            assertThat(like.userId).isEqualTo(2L)
            assertThat(like.createdAt).isNotNull()
        }
    }
}
