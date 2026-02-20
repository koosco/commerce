package com.koosco.orderservice.application.port

import com.koosco.orderservice.domain.entity.OrderIdempotency

interface OrderIdempotencyRepository {

    fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String): OrderIdempotency?

    fun save(entry: OrderIdempotency): OrderIdempotency
}
