package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.domain.entity.OrderIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderApiIdempotencyRepository : JpaRepository<OrderIdempotency, Long> {

    fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String): OrderIdempotency?
}
