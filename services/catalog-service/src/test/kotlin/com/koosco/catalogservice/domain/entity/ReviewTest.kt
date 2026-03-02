package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.ContentStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Review 도메인 테스트")
class ReviewTest {

    @Nested
    @DisplayName("create 메서드는")
    inner class CreateTest {

        @Test
        fun `리뷰를 생성한다`() {
            val review = Review.create(
                productId = 1L,
                userId = 1L,
                orderItemId = 1L,
                title = "좋은 상품",
                content = "만족합니다",
                rating = 5,
            )

            assertThat(review.productId).isEqualTo(1L)
            assertThat(review.userId).isEqualTo(1L)
            assertThat(review.rating).isEqualTo(5)
            assertThat(review.status).isEqualTo(ContentStatus.VISIBLE)
        }

        @Test
        fun `평점이 1 미만이면 예외를 던진다`() {
            assertThatThrownBy {
                Review.create(1L, 1L, null, "제목", "내용", 0)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `평점이 5 초과이면 예외를 던진다`() {
            assertThatThrownBy {
                Review.create(1L, 1L, null, "제목", "내용", 6)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    inner class UpdateTest {

        @Test
        fun `제목과 내용을 변경한다`() {
            val review = Review.create(1L, 1L, null, "원래 제목", "원래 내용", 3)

            review.update(title = "변경된 제목", content = "변경된 내용", rating = null)

            assertThat(review.title).isEqualTo("변경된 제목")
            assertThat(review.content).isEqualTo("변경된 내용")
            assertThat(review.rating).isEqualTo(3)
        }

        @Test
        fun `평점을 변경한다`() {
            val review = Review.create(1L, 1L, null, "제목", "내용", 3)

            review.update(title = null, content = null, rating = 5)

            assertThat(review.rating).isEqualTo(5)
        }

        @Test
        fun `평점이 범위를 벗어나면 예외를 던진다`() {
            val review = Review.create(1L, 1L, null, "제목", "내용", 3)

            assertThatThrownBy { review.update(null, null, 0) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("softDelete 메서드는")
    inner class SoftDeleteTest {

        @Test
        fun `상태를 DELETED로 변경한다`() {
            val review = Review.create(1L, 1L, null, "제목", "내용", 3)

            review.softDelete()

            assertThat(review.status).isEqualTo(ContentStatus.DELETED)
        }
    }

    @Nested
    @DisplayName("addImage 메서드는")
    inner class AddImageTest {

        @Test
        fun `이미지를 추가한다`() {
            val review = Review.create(1L, 1L, null, "제목", "내용", 3)

            review.addImage("http://image.jpg", 0)

            assertThat(review.images).hasSize(1)
            assertThat(review.images.first().imageUrl).isEqualTo("http://image.jpg")
        }
    }
}
