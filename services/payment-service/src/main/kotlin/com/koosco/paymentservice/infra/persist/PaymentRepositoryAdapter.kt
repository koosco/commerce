package com.koosco.paymentservice.infra.persist

import com.koosco.paymentservice.application.port.PaymentRepositoryPort
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.infra.persist.jpa.JpaPaymentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * fileName       : PaymentRepositoryAdapter
 * author         : koo
 * date           : 2025. 12. 24. 오전 4:49
 * description    :
 */
@Repository
class PaymentRepositoryAdapter(private val jpaRepository: JpaPaymentRepository) : PaymentRepositoryPort {

    override fun findByPaymentId(paymentId: UUID): Payment? = jpaRepository.findByIdOrNull(paymentId)

    override fun existsByOrderId(orderId: Long): Boolean = jpaRepository.existsByOrderId(orderId)

    override fun save(payment: Payment): Payment = jpaRepository.save(payment)
}
