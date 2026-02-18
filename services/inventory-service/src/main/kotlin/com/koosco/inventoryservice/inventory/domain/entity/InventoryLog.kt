package com.koosco.inventoryservice.inventory.domain.entity

import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction
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
    name = "inventory_log",
    indexes = [
        Index(name = "idx_inventory_log_sku_id", columnList = "sku_id"),
        Index(name = "idx_inventory_log_order_id", columnList = "order_id"),
        Index(name = "idx_inventory_log_created_at", columnList = "created_at"),
    ],
)
class InventoryLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "sku_id", nullable = false, length = 50)
    val skuId: String,

    @Column(name = "order_id")
    val orderId: Long?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val action: InventoryAction,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
