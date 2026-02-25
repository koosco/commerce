package com.koosco.userservice.application.port

import com.koosco.userservice.domain.entity.UserIdempotency

interface UserIdempotencyRepository {
    fun findByIdempotencyKeyAndResourceType(idempotencyKey: String, resourceType: String): UserIdempotency?
    fun save(entry: UserIdempotency): UserIdempotency
}
