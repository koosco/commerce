package com.koosco.orderservice.infra.idempotency

import com.koosco.orderservice.domain.entity.OrderEventIdempotency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for OrderEventIdempotency.
 */
@Repository
interface JpaOrderIdempotencyRepository :
    JpaRepository<OrderEventIdempotency, Long>,
    OrderIdempotencyRepository {
    override fun findByMessageIdAndAction(messageId: String, action: String): OrderEventIdempotency?
}
