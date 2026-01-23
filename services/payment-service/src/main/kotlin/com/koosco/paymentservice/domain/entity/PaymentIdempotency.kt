package com.koosco.paymentservice.domain.entity

import com.koosco.paymentservice.domain.enums.PaymentAction
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "payment_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_payment_idempotency",
            columnNames = ["order_id", "action", "idempotency_key"],
        ),
    ],
)
class PaymentIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    val action: PaymentAction,

    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
