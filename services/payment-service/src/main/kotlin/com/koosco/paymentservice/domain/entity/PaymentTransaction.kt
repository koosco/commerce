package com.koosco.paymentservice.domain.entity

import com.koosco.paymentservice.domain.vo.Money
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "payment_transaction")
class PaymentTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    val payment: Payment,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: PaymentTransactionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: PaymentTransactionStatus,

    @Column(name = "pg_transaction_id")
    val pgTransactionId: String?,

    @Embedded
    val amount: Money,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: LocalDateTime = LocalDateTime.now(),
)

enum class PaymentTransactionType {
    APPROVAL,
    CANCEL,
}

enum class PaymentTransactionStatus {
    SUCCESS,
    FAILED,
}
