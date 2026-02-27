package com.koosco.inventoryservice.infra.batch

import com.koosco.inventoryservice.application.port.InventorySnapshotWriter
import com.koosco.inventoryservice.application.port.InventoryStockSnapshotQueryPort
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * fileName       : InventorySnapshotScheduler
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:09
 * description    :
 */
@Profile("batch")
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
