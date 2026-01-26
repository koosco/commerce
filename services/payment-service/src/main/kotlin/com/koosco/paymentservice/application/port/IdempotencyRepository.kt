package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.domain.entity.PaymentIdempotency

interface IdempotencyRepository {
    fun save(paymentIdempotency: PaymentIdempotency)
}
