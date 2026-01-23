package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.domain.entity.Payment
import java.util.UUID

interface PaymentRepositoryPort {

    fun findByPaymentId(paymentId: UUID): Payment?

    fun existsByOrderId(orderId: Long): Boolean

    fun save(payment: Payment): Payment
}
