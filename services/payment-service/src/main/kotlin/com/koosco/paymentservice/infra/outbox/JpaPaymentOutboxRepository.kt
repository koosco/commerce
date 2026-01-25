package com.koosco.paymentservice.infra.outbox

import com.koosco.paymentservice.domain.entity.PaymentOutboxEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for PaymentOutboxEntry.
 */
@Repository
interface JpaPaymentOutboxRepository :
    JpaRepository<PaymentOutboxEntry, Long>,
    PaymentOutboxRepository
