package com.koosco.orderservice.order.domain

import com.koosco.common.core.event.DomainEvent
import com.koosco.orderservice.order.domain.enums.OrderCancelReason
import com.koosco.orderservice.order.domain.event.OrderCancelled
import com.koosco.orderservice.order.domain.event.OrderItemsRefunded
import com.koosco.orderservice.order.domain.event.OrderPaid
import com.koosco.orderservice.order.domain.event.OrderPlaced
import com.koosco.orderservice.order.domain.exception.InvalidOrderStatus
import com.koosco.orderservice.order.domain.exception.PaymentMisMatch
import com.koosco.orderservice.order.domain.vo.Money
import com.koosco.orderservice.order.domain.vo.OrderAmount
import com.koosco.orderservice.order.domain.vo.OrderItemSpec
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Transient
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus,

    /** 주문 원금 (아이템 합계) */
    @Column(nullable = false)
    val totalAmount: Money,

    /** 쿠폰으로 할인된 총 금액 */
    @Column(nullable = false)
    val discountAmount: Money,

    /** 실제 결제 요청 금액 */
    @Column(nullable = false)
    val payableAmount: Money,

    /** 누적 환불 금액 */
    @Column(nullable = false)
    var refundedAmount: Money = Money(0L),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val items: MutableList<OrderItem> = mutableListOf(),
) {

    @Transient
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    companion object {
        fun create(userId: Long, itemSpecs: List<OrderItemSpec>, amount: OrderAmount): Order {
            val order = Order(
                userId = userId,
                status = OrderStatus.INIT,
                totalAmount = amount.total,
                discountAmount = amount.discount,
                payableAmount = amount.payable,
                items = mutableListOf(),
            )

            itemSpecs.forEach { spec ->
                OrderItem.create(order, spec, amount).also {
                    order.items.add(it)
                }
            }

            return order
        }
    }

    fun pullDomainEvents(): List<DomainEvent> = domainEvents.toList().also { domainEvents.clear() }

    /**
     * ==== ORDER FLOW ====
     */
    fun place() {
        if (status != OrderStatus.INIT) {
            throw InvalidOrderStatus()
        }

        val orderId = requireNotNull(id) {
            "Order must be persisted before placing"
        }

        status = OrderStatus.CREATED
        updatedAt = LocalDateTime.now()

        domainEvents.add(
            OrderPlaced(
                orderId = orderId,
                userId = userId,
                totalAmount = totalAmount.amount,
                payableAmount = payableAmount.amount,
                items = items.map {
                    Item(
                        skuId = it.skuId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice.amount,
                    )
                },
            ),
        )
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
    }

    fun markPaid(paidAmount: Money) {
        if (status != OrderStatus.PAYMENT_PENDING) {
            // 결제 대기 상태에서만 상태 변경 가능
            throw InvalidOrderStatus()
        }

        if (paidAmount != payableAmount) {
            throw PaymentMisMatch()
        }

        status = OrderStatus.PAID
        updatedAt = LocalDateTime.now()

        domainEvents.add(
            OrderPaid(
                orderId = id!!,
                paidAmount = payableAmount.amount,
                items = items.map {
                    Item(
                        skuId = it.skuId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice.amount,
                    )
                },
            ),
        )
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
        updatedAt = LocalDateTime.now()

        domainEvents.add(
            OrderCancelled(
                orderId = id!!,
                reason = reason,
                items = items.map {
                    Item(
                        skuId = it.skuId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice.amount,
                    )
                },
            ),
        )
    }

    /**
     * 재고 예약 실패로 인한 주문 실패 처리
     * CREATED 상태에서만 FAILED로 전이 가능
     */
    fun markFailed(reason: OrderCancelReason) {
        if (status != OrderStatus.CREATED) {
            throw InvalidOrderStatus("재고 예약 실패 처리는 CREATED 상태에서만 가능합니다. 현재 상태: $status")
        }

        status = OrderStatus.FAILED
        updatedAt = LocalDateTime.now()
    }

    /**
     * 재고 확정 실패로 인한 주문 취소 처리
     * PAID 상태에서만 CANCELLED로 전이 가능 (환불 필요)
     */
    fun cancelByStockConfirmFailure() {
        if (status != OrderStatus.PAID) {
            throw InvalidOrderStatus("재고 확정 실패 처리는 PAID 상태에서만 가능합니다. 현재 상태: $status")
        }

        status = OrderStatus.CANCELLED
        updatedAt = LocalDateTime.now()

        domainEvents.add(
            OrderCancelled(
                orderId = id!!,
                reason = OrderCancelReason.STOCK_CONFIRM_FAILED,
                items = items.map {
                    Item(
                        skuId = it.skuId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice.amount,
                    )
                },
            ),
        )
    }

    /**
     * ==== REFUND ====
     */
    fun refundAll(itemIds: List<Long>): Money {
        val totalRefundAmount = itemIds.fold(Money.ZERO) { acc, itemId ->
            val item = items.first { it.id == itemId }
            acc + item.refund()
        }

        itemIds.forEach { itemId ->
            refundItem(itemId)
        }

        return totalRefundAmount
    }

    fun refundItem(itemId: Long): Money {
        if (status == OrderStatus.PAID || status == OrderStatus.CONFIRMED || status == OrderStatus.PARTIALLY_REFUNDED) {
            throw InvalidOrderStatus("환불 가능한 상태가 아닙니다. 현재 상태: $status")
        }

        val item = items.first { it.id == itemId }
        val refundAmount = item.refund()

        refundedAmount = refundedAmount + refundAmount
        updatedAt = LocalDateTime.now()

        status = if (items.all { it.status == OrderItemStatus.REFUNDED }) {
            OrderStatus.REFUNDED
        } else {
            OrderStatus.PARTIALLY_REFUNDED
        }

        domainEvents.add(
            OrderItemsRefunded(
                orderId = id!!,
                refundedAmount = refundAmount.amount,
                refundedItems = listOf(
                    Item(
                        skuId = item.skuId,
                        quantity = item.quantity,
                        unitPrice = refundAmount.amount,
                    ),
                ),
            ),
        )

        return refundAmount
    }
}
