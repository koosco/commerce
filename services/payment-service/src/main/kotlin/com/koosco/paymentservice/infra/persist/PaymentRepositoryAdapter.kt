package com.koosco.paymentservice.infra.persist

import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.infra.persist.jpa.JpaPaymentRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * fileName       : PaymentRepositoryAdapter
 * author         : koo
 * date           : 2025. 12. 24. 오전 4:49
 * description    :
 */
@Repository
class PaymentRepositoryAdapter(private val jpaRepository: JpaPaymentRepository) : PaymentRepository {

    override fun findByPaymentId(paymentId: UUID): Payment? = jpaRepository.findByPaymentId(paymentId)

    override fun findByOrderId(orderId: Long): Payment? = jpaRepository.findByOrderId(orderId)

    override fun existsByOrderId(orderId: Long): Boolean = jpaRepository.existsByOrderId(orderId)

    override fun save(payment: Payment): Payment = jpaRepository.save(payment)
}
