package com.koosco.inventoryservice.infra.storage.secondary

import com.koosco.inventoryservice.domain.entity.InventorySnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * fileName       : JpaInventorySnapshotRepository
 * author         : koo
 * date           : 2025. 12. 29. 오후 11:56
 * description    :
 */
interface JpaInventorySnapshotRepository : JpaRepository<InventorySnapshot, String> {

    @Modifying
    @Query(
        value = """
        INSERT INTO inventory_snapshot(sku_id, total, reserved, snapshotted_at)
        VALUES (:skuId, :total, :reserved, NOW())
        ON DUPLICATE KEY UPDATE
          total = :total,
          reserved = :reserved,
          snapshotted_at = NOW()
    """,
        nativeQuery = true,
    )
    fun upsert(skuId: String, total: Int, reserved: Int)
}
