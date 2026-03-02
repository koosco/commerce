package com.koosco.userservice.domain

import com.koosco.userservice.domain.entity.UserIdempotency
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("UserIdempotency 도메인 엔티티")
class UserIdempotencyTest {

    @Test
    fun `UserIdempotency를 생성한다`() {
        val idempotency = UserIdempotency.create("key-123", UserIdempotency.USER, 1L)

        assertThat(idempotency.idempotencyKey).isEqualTo("key-123")
        assertThat(idempotency.resourceType).isEqualTo("USER")
        assertThat(idempotency.resourceId).isEqualTo(1L)
        assertThat(idempotency.createdAt).isNotNull()
    }

    @Test
    fun `ADDRESS 타입으로 생성한다`() {
        val idempotency = UserIdempotency.create("key-456", UserIdempotency.ADDRESS, 2L)

        assertThat(idempotency.resourceType).isEqualTo("ADDRESS")
        assertThat(idempotency.resourceId).isEqualTo(2L)
    }

    @Test
    fun `상수값이 올바르다`() {
        assertThat(UserIdempotency.USER).isEqualTo("USER")
        assertThat(UserIdempotency.ADDRESS).isEqualTo("ADDRESS")
    }
}
