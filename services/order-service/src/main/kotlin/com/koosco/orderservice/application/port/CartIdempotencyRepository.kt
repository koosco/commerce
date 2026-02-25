package com.koosco.orderservice.application.port

import com.koosco.orderservice.domain.entity.CartIdempotency

interface CartIdempotencyRepository {

    fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String): CartIdempotency?

    fun save(entry: CartIdempotency): CartIdempotency
}
