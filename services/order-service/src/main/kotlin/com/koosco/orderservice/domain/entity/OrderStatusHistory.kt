package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.enums.OrderStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "order_status_history",
    indexes = [
        Index(name = "idx_order_status_history_order", columnList = "order_id"),
    ],
)
class OrderStatusHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    val fromStatus: OrderStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    val toStatus: OrderStatus,

    @Column(name = "reason")
    val reason: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            orderId: Long,
            fromStatus: OrderStatus?,
            toStatus: OrderStatus,
            reason: String? = null,
        ): OrderStatusHistory = OrderStatusHistory(
            orderId = orderId,
            fromStatus = fromStatus,
            toStatus = toStatus,
            reason = reason,
        )
    }
}
