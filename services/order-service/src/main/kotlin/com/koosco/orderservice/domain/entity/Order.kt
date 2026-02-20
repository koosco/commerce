package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.exception.InvalidOrderStatus
import com.koosco.orderservice.domain.exception.PaymentMisMatch
import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_order")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 40, unique = true)
    val orderNo: String,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus,

    @Column(nullable = false, length = 3)
    val currency: String = "KRW",

    /** 아이템 합계 (할인/배송비 미포함) */
    @Column(nullable = false)
    val subtotalAmount: Money,

    /** 쿠폰 등 할인 금액 */
    @Column(nullable = false)
    val discountAmount: Money,

    /** 배송비 */
    @Column(nullable = false)
    val shippingFee: Money,

    /** 최종 결제 금액 = subtotal - discount + shippingFee */
    @Column(nullable = false)
    val totalAmount: Money,

    /** 배송지 JSON 스냅샷 */
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    val shippingAddressSnapshot: String,

    /** 주문 시점 가격 JSON 스냅샷 */
    @Lob
    @Column(columnDefinition = "TEXT")
    val pricingSnapshot: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var placedAt: LocalDateTime? = null,

    var paidAt: LocalDateTime? = null,

    var canceledAt: LocalDateTime? = null,

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val items: MutableList<OrderItem> = mutableListOf(),
) {

    companion object {
        fun create(
            orderNo: String,
            userId: Long,
            itemSpecs: List<OrderItemSpec>,
            amount: OrderAmount,
            shippingAddressSnapshot: String,
            pricingSnapshot: String? = null,
        ): Order {
            val order = Order(
                orderNo = orderNo,
                userId = userId,
                status = OrderStatus.CREATED,
                subtotalAmount = amount.subtotal,
                discountAmount = amount.discount,
                shippingFee = amount.shippingFee,
                totalAmount = amount.total,
                shippingAddressSnapshot = shippingAddressSnapshot,
                pricingSnapshot = pricingSnapshot,
                placedAt = LocalDateTime.now(),
            )

            itemSpecs.forEach { spec ->
                OrderItem.create(order, spec).also {
                    order.items.add(it)
                }
            }

            return order
        }
    }

    fun markReserved() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAYMENT_CREATED) {
            throw InvalidOrderStatus()
        }

        status = OrderStatus.RESERVED
        updatedAt = LocalDateTime.now()
    }

    fun markPaymentCreated() {
        if (status != OrderStatus.RESERVED && status != OrderStatus.CREATED) {
            throw InvalidOrderStatus()
        }

        status = OrderStatus.PAYMENT_CREATED
        updatedAt = LocalDateTime.now()
    }

    fun markPaymentPending() {
        if (status != OrderStatus.RESERVED) {
            throw InvalidOrderStatus()
        }
        status = OrderStatus.PAYMENT_PENDING
        updatedAt = LocalDateTime.now()
    }

    fun markPaid(paidAmount: Money) {
        if (status != OrderStatus.PAYMENT_PENDING) {
            throw InvalidOrderStatus()
        }

        if (paidAmount != totalAmount) {
            throw PaymentMisMatch()
        }

        status = OrderStatus.PAID
        paidAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    fun confirmStock() {
        if (status != OrderStatus.PAID) {
            throw InvalidOrderStatus("재고 확정은 결제 완료 상태에서만 가능합니다. 현재 상태: $status")
        }

        status = OrderStatus.CONFIRMED
        updatedAt = LocalDateTime.now()
    }

    fun cancel(reason: OrderCancelReason) {
        if (status != OrderStatus.PAYMENT_PENDING) {
            throw InvalidOrderStatus()
        }

        status = OrderStatus.CANCELLED
        canceledAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    fun markFailed(reason: OrderCancelReason) {
        if (status != OrderStatus.CREATED) {
            throw InvalidOrderStatus("재고 예약 실패 처리는 CREATED 상태에서만 가능합니다. 현재 상태: $status")
        }

        status = OrderStatus.FAILED
        updatedAt = LocalDateTime.now()
    }

    fun cancelByStockConfirmFailure() {
        if (status != OrderStatus.PAID) {
            throw InvalidOrderStatus("재고 확정 실패 처리는 PAID 상태에서만 가능합니다. 현재 상태: $status")
        }

        status = OrderStatus.CANCELLED
        canceledAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
}
