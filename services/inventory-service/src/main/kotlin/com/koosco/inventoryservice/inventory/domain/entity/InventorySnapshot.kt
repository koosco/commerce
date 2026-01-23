package com.koosco.inventoryservice.inventory.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * fileName       : InventorySnapshot
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:15
 * description    :
 */
@Entity
@Table(name = "inventory_snapshot")
class InventorySnapshot(
    @Id
    @Column(name = "sku_id")
    val skuId: String,

    @Column(nullable = false)
    var total: Int,

    @Column(nullable = false)
    var reserved: Int,

    @Column(nullable = false)
    var snapshottedAt: LocalDateTime,
)
