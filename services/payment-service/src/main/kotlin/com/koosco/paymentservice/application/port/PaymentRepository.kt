package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.domain.entity.Payment
import java.util.UUID

interface PaymentRepository {

    fun findByPaymentId(paymentId: UUID): Payment?

    fun findByOrderId(orderId: Long): Payment?

    fun existsByOrderId(orderId: Long): Boolean

    fun save(payment: Payment): Payment
}
