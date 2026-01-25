package com.koosco.orderservice.order.infra.outbox

import com.koosco.orderservice.order.domain.entity.OrderOutboxEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for OrderOutboxEntry.
 */
@Repository
interface JpaOrderOutboxRepository :
    JpaRepository<OrderOutboxEntry, Long>,
    OrderOutboxRepository
