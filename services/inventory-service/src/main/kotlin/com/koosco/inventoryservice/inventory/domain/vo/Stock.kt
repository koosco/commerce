package com.koosco.inventoryservice.inventory.domain.vo

import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Stock(
    @Column(name = "total_stock", nullable = false)
    val total: Int,

    @Column(name = "reserved_stock", nullable = false)
    val reserved: Int = 0,
) {
    val available: Int
        get() = total - reserved

    init {
        if (total < 0 ||
            reserved < 0 ||
            total < reserved
        ) {
            throw NotEnoughStockException("Invalid stock state: total=$total, reserved=$reserved")
        }
    }

    fun increase(q: Int): Stock {
        if (q <= 0) throw NotEnoughStockException("Increase quantity must be positive")
        return copy(total = total + q)
    }

    fun decrease(q: Int): Stock {
        if (q <= 0) throw NotEnoughStockException("Decrease quantity must be positive")
        val newTotal = total - q
        if (newTotal < reserved) {
            throw NotEnoughStockException("Not enough available stock")
        }
        return copy(total = newTotal)
    }

    fun reserve(q: Int): Stock {
        if (q <= 0 || available < q) {
            throw NotEnoughStockException("Not enough available stock")
        }
        return copy(reserved = reserved + q)
    }

    fun confirm(q: Int): Stock {
        if (q <= 0 || reserved < q) {
            throw NotEnoughStockException("Not enough reserved stock")
        }
        return copy(
            total = total - q,
            reserved = reserved - q,
        )
    }

    fun cancelReservation(q: Int): Stock {
        if (q <= 0 || reserved < q) {
            throw NotEnoughStockException("Not enough reserved stock")
        }
        return copy(reserved = reserved - q)
    }
}
