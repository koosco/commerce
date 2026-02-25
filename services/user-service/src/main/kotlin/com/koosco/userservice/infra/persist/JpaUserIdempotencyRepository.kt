package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.UserIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserIdempotencyRepository : JpaRepository<UserIdempotency, Long> {
    fun findByIdempotencyKeyAndResourceType(idempotencyKey: String, resourceType: String): UserIdempotency?
}
