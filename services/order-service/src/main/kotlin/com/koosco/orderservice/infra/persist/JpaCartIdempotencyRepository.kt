package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.domain.entity.CartIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCartIdempotencyRepository : JpaRepository<CartIdempotency, Long> {
    fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String): CartIdempotency?
}
