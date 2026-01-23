package com.koosco.inventoryservice.inventory.application.port

/**
 * fileName       : InventoryStockQueryPort
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:52
 * description    :
 */
interface InventoryStockQueryPort {

    fun getStock(skuId: String): StockView

    fun getStocks(skuIds: List<String>): List<StockView>

    data class StockView(val skuId: String, val total: Int, val reserved: Int, val available: Int)
}
