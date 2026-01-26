package com.koosco.paymentservice.infra.persist

import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import com.koosco.paymentservice.infra.persist.jpa.JpaIdempotencyRepository
import org.springframework.stereotype.Repository

@Repository
class IdempotencyRepositoryAdapter(private val jpaRepository: JpaIdempotencyRepository) : IdempotencyRepository {
    override fun save(paymentIdempotency: PaymentIdempotency) {
        jpaRepository.save(paymentIdempotency)
    }
}
