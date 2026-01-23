package com.koosco.inventoryservice.inventory.infra.storage.secondary

import com.koosco.inventoryservice.inventory.application.port.InventorySnapshotWriter
import org.springframework.stereotype.Component

/**
 * fileName       : JpaInventorySnapshotWriter
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:11
 * description    :
 */
@Component
class JpaInventorySnapshotWriter(private val jpaRepository: JpaInventorySnapshotRepository) : InventorySnapshotWriter {

    override fun write(skuId: String, total: Int, reserved: Int) {
        jpaRepository.upsert(
            skuId = skuId,
            total = total,
            reserved = reserved,
        )
    }
}
