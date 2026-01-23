package com.koosco.paymentservice.infra.persist.jpa

import com.koosco.paymentservice.domain.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * fileName       : JpaPaymentRepository
 * author         : koo
 * date           : 2025. 12. 24. 오전 4:49
 * description    :
 */
interface JpaPaymentRepository : JpaRepository<Payment, UUID> {
    fun existsByOrderId(orderId: Long): Boolean
}
