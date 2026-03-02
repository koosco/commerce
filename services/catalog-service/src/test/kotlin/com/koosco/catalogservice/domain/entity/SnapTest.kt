package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.ContentStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Snap 도메인 테스트")
class SnapTest {

    @Nested
    @DisplayName("create 메서드는")
    inner class CreateTest {

        @Test
        fun `스냅을 생성한다`() {
            val snap = Snap.create(productId = 1L, userId = 1L, caption = "멋진 상품")

            assertThat(snap.productId).isEqualTo(1L)
            assertThat(snap.userId).isEqualTo(1L)
            assertThat(snap.caption).isEqualTo("멋진 상품")
            assertThat(snap.status).isEqualTo(ContentStatus.VISIBLE)
        }

        @Test
        fun `캡션 없이 스냅을 생성한다`() {
            val snap = Snap.create(productId = 1L, userId = 1L, caption = null)

            assertThat(snap.caption).isNull()
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    inner class UpdateTest {

        @Test
        fun `캡션을 변경한다`() {
            val snap = Snap.create(1L, 1L, "원래 캡션")

            snap.update(caption = "변경된 캡션")

            assertThat(snap.caption).isEqualTo("변경된 캡션")
        }

        @Test
        fun `null이면 변경하지 않는다`() {
            val snap = Snap.create(1L, 1L, "원래 캡션")

            snap.update(caption = null)

            assertThat(snap.caption).isEqualTo("원래 캡션")
        }
    }

    @Nested
    @DisplayName("softDelete 메서드는")
    inner class SoftDeleteTest {

        @Test
        fun `상태를 DELETED로 변경한다`() {
            val snap = Snap.create(1L, 1L, "캡션")

            snap.softDelete()

            assertThat(snap.status).isEqualTo(ContentStatus.DELETED)
        }
    }

    @Nested
    @DisplayName("addImage 메서드는")
    inner class AddImageTest {

        @Test
        fun `이미지를 추가한다`() {
            val snap = Snap.create(1L, 1L, "캡션")

            snap.addImage("http://image.jpg", 0)

            assertThat(snap.images).hasSize(1)
            assertThat(snap.images.first().imageUrl).isEqualTo("http://image.jpg")
        }
    }
}
