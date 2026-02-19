package com.koosco.orderservice.infra.outbox

import com.koosco.orderservice.domain.entity.OrderOutboxEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for OrderOutboxEntry.
 */
@Repository
interface JpaOrderOutboxRepository :
    JpaRepository<OrderOutboxEntry, Long>,
    OrderOutboxRepository
