package com.koosco.orderservice.order.domain

import com.koosco.orderservice.order.domain.vo.Money
import com.koosco.orderservice.order.domain.vo.OrderAmount
import com.koosco.orderservice.order.domain.vo.OrderItemSpec
import jakarta.persistence.Column
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
@Table(name = "order_items")
class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,

    @Column(nullable = false)
    val skuId: String,

    @Column(nullable = false)
    val quantity: Int,

    /** 단가 */
    @Column(nullable = false)
    val unitPrice: Money,

    /** 수량 * 단가 */
    @Column(nullable = false)
    val totalPrice: Money,

    /** 이 아이템에 분배된 할인 금액 */
    @Column(nullable = false)
    val discountAmount: Money,

    /** 환불 가능 금액 (고정값) */
    @Column(nullable = false)
    val refundableAmount: Money,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderItemStatus,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(order: Order, spec: OrderItemSpec, amount: OrderAmount): OrderItem = OrderItem(
            order = order,
            skuId = spec.skuId,
            quantity = spec.quantity,
            unitPrice = spec.unitPrice,
            totalPrice = spec.totalPrice(),
            discountAmount = spec.discountAmount(
                amount.total,
                amount.discount,
            ),
            refundableAmount = spec.refundableAmount(
                amount.total,
                amount.discount,
            ),
            status = OrderItemStatus.ORDERED,
        )
    }

    fun refund(): Money {
        require(status == OrderItemStatus.ORDERED) { "환불은 ORDERED 상태에서만 가능합니다." }
        status = OrderItemStatus.REFUNDED
        updatedAt = LocalDateTime.now()
        return refundableAmount
    }

    fun canRefund(): Boolean = status == OrderItemStatus.ORDERED
}
