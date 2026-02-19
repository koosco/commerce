package com.koosco.inventoryservice.domain.entity

import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import com.koosco.inventoryservice.domain.vo.Stock
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventory")
class Inventory(

    /**
     * Stock Keeping Unit ID
     */
    @Id
    @Column(name = "sku_id", length = 50)
    val skuId: String,

    @Embedded
    var stock: Stock,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    @PreUpdate
    fun onUpdate() {
        this.updatedAt = LocalDateTime.now()
    }

    fun updateStock(quantity: Int) {
        stock = Stock(quantity, this.stock.reserved)
    }

    /** 재고 증가 */
    fun increase(quantity: Int) {
        stock = stock.increase(quantity)
    }

    /** 재고 감소 (출고/폐기 등의 이유) */
    fun decrease(quantity: Int) {
        stock = stock.decrease(quantity)
    }

    /** 재고 예약 (주문 생성 시) */
    fun reserve(quantity: Int) {
        try {
            stock = stock.reserve(quantity)
        } catch (e: NotEnoughStockException) {
            throw NotEnoughStockException(
                message = "Not enough stock for skuId=$skuId: requested=$quantity, available=${stock.available}",
                skuId = this.skuId,
                requestedQuantity = quantity,
                availableQuantity = stock.available,
            )
        }
    }

    /** 예약 재고 확정 (결제 성공 시) */
    fun confirm(quantity: Int) {
        stock = stock.confirm(quantity)
    }

    /** 예약 취소 (결제 실패/주문 취소) */
    fun cancelReservation(quantity: Int) {
        stock = stock.cancelReservation(quantity)
    }
}
