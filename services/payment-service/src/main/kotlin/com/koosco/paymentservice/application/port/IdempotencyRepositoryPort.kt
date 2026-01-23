package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.domain.entity.PaymentIdempotency

interface IdempotencyRepositoryPort {
    fun save(paymentIdempotency: PaymentIdempotency)
}
