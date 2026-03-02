package com.koosco.userservice.domain

import com.koosco.userservice.domain.entity.LoginHistory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LoginHistory 도메인 엔티티")
class LoginHistoryTest {

    @Nested
    @DisplayName("success")
    inner class Success {

        @Test
        fun `성공 로그인 히스토리를 생성한다`() {
            val history = LoginHistory.success(1L, "127.0.0.1", "Mozilla/5.0")

            assertThat(history.userId).isEqualTo(1L)
            assertThat(history.ip).isEqualTo("127.0.0.1")
            assertThat(history.userAgent).isEqualTo("Mozilla/5.0")
            assertThat(history.success).isTrue()
            assertThat(history.failureReason).isNull()
        }

        @Test
        fun `userAgent가 null이어도 생성된다`() {
            val history = LoginHistory.success(1L, "127.0.0.1", null)

            assertThat(history.userAgent).isNull()
            assertThat(history.success).isTrue()
        }
    }

    @Nested
    @DisplayName("failure")
    inner class Failure {

        @Test
        fun `실패 로그인 히스토리를 생성한다`() {
            val history = LoginHistory.failure(1L, "127.0.0.1", "Mozilla/5.0", "INVALID_PASSWORD")

            assertThat(history.userId).isEqualTo(1L)
            assertThat(history.ip).isEqualTo("127.0.0.1")
            assertThat(history.userAgent).isEqualTo("Mozilla/5.0")
            assertThat(history.success).isFalse()
            assertThat(history.failureReason).isEqualTo("INVALID_PASSWORD")
        }
    }
}
