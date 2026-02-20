package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.application.port.OrderIdempotencyRepository
import com.koosco.orderservice.domain.entity.OrderIdempotency
import org.springframework.stereotype.Repository

@Repository
class OrderIdempotencyRepositoryAdapter(
    private val jpaOrderApiIdempotencyRepository: JpaOrderApiIdempotencyRepository,
) : OrderIdempotencyRepository {

    override fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String): OrderIdempotency? =
        jpaOrderApiIdempotencyRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey)

    override fun save(entry: OrderIdempotency): OrderIdempotency = jpaOrderApiIdempotencyRepository.save(entry)
}
