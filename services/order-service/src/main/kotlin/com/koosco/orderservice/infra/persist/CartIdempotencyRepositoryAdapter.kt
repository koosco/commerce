package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.application.port.CartIdempotencyRepository
import com.koosco.orderservice.domain.entity.CartIdempotency
import org.springframework.stereotype.Repository

@Repository
class CartIdempotencyRepositoryAdapter(private val jpaRepository: JpaCartIdempotencyRepository) :
    CartIdempotencyRepository {
    override fun findByUserIdAndIdempotencyKey(userId: Long, idempotencyKey: String) =
        jpaRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey)

    override fun save(entry: CartIdempotency) = jpaRepository.save(entry)
}
