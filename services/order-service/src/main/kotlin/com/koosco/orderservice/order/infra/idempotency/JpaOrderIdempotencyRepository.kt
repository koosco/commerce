package com.koosco.orderservice.order.infra.idempotency

import com.koosco.orderservice.order.domain.entity.OrderEventIdempotency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for OrderEventIdempotency.
 */
@Repository
interface JpaOrderIdempotencyRepository :
    JpaRepository<OrderEventIdempotency, Long>,
    OrderIdempotencyRepository {
    override fun findByEventIdAndAction(eventId: String, action: String): OrderEventIdempotency?
}
