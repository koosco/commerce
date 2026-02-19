package com.koosco.inventoryservice.application.port

/**
 * fileName       : InventoryStockSnapshotQueryPort
 * author         : koo
 * date           : 2025. 12. 29. 오후 11:50
 * description    :
 */
interface InventoryStockSnapshotQueryPort {

    fun getAllStocks(): List<SnapshotStockView>

    data class SnapshotStockView(val skuId: String, val total: Int, val reserved: Int)
}
