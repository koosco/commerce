package com.koosco.paymentservice.infra.outbox

import com.koosco.paymentservice.domain.entity.PaymentOutboxEntry

/**
 * Repository for managing PaymentOutboxEntry entities.
 */
interface PaymentOutboxRepository {
    /**
     * Save an outbox entry.
     * Should be called within the same transaction as the domain operation.
     */
    fun save(entry: PaymentOutboxEntry): PaymentOutboxEntry
}
