package com.koosco.inventoryservice.application.port

/**
 * fileName       : InventorySeedPort
 * author         : koo
 * date           : 2025. 12. 30. 오전 12:06
 * description    :
 */
interface InventorySeedPort {

    fun initStock(skuId: String, initialQuantity: Int)

    fun initStocks(skuIds: List<String>, initialQuantity: Int)

    fun clear()
}
