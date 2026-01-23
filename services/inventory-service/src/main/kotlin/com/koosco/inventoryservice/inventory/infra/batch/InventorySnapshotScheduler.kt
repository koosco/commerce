package com.koosco.inventoryservice.inventory.infra.batch

import com.koosco.inventoryservice.inventory.application.port.InventorySnapshotWriter
import com.koosco.inventoryservice.inventory.application.port.InventoryStockSnapshotQueryPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * fileName       : InventorySnapshotScheduler
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:09
 * description    :
 */
@Component
class InventorySnapshotScheduler(
    private val snapshotQuery: InventoryStockSnapshotQueryPort,
    private val snapshotWriter: InventorySnapshotWriter,
) {
    @Scheduled(fixedDelay = 60_000)
    fun snapshot() {
        val stocks = snapshotQuery.getAllStocks().forEach {
            snapshotWriter.write(
                skuId = it.skuId,
                total = it.total,
                reserved = it.reserved,
            )
        }
    }
}
