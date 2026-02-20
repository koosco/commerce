package com.koosco.orderservice.infra.idempotency

import com.koosco.orderservice.domain.entity.OrderEventIdempotency

/**
 * Repository for managing OrderEventIdempotency entities.
 */
interface OrderIdempotencyRepository {
    fun existsByMessageIdAndAction(messageId: String, action: String): Boolean
    fun save(entry: OrderEventIdempotency): OrderEventIdempotency
    fun findByMessageIdAndAction(messageId: String, action: String): OrderEventIdempotency?
}
