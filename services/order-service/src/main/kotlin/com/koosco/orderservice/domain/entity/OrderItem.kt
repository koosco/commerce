package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderItemSpec
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_item")
class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,

    @Column(nullable = false)
    val skuId: Long,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val brandId: Long,

    @Column(nullable = false)
    val titleSnapshot: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    val optionSnapshot: String? = null,

    @Column(nullable = false)
    val qty: Int,

    @Column(nullable = false)
    val unitPrice: Money,

    @Column(nullable = false)
    val lineAmount: Money,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(order: Order, spec: OrderItemSpec): OrderItem = OrderItem(
            order = order,
            skuId = spec.skuId,
            productId = spec.productId,
            brandId = spec.brandId,
            titleSnapshot = spec.titleSnapshot,
            optionSnapshot = spec.optionSnapshot,
            qty = spec.quantity,
            unitPrice = spec.unitPrice,
            lineAmount = spec.totalPrice(),
        )
    }
}
