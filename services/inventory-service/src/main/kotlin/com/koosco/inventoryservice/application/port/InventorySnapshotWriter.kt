package com.koosco.inventoryservice.application.port

/**
 * fileName       : InventorySnapshotWriter
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:08
 * description    :
 */
interface InventorySnapshotWriter {
    fun write(skuId: String, total: Int, reserved: Int)
}
