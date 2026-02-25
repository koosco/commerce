package com.koosco.paymentservice.infra.persist.jpa

import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaIdempotencyRepository : JpaRepository<PaymentIdempotency, Long> {
    fun existsByMessageIdAndAction(messageId: String, action: String): Boolean
}
