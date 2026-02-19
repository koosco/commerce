package com.koosco.inventoryservice.application.port

/**
 * fileName       : InventoryStorePort
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:10
 * description    :
 */
interface InventoryStockStorePort {

    fun initialize(skuId: String, initialQuantity: Int)

    fun add(items: List<AddItem>)

    fun reserve(orderId: Long, items: List<ReserveItem>)

    fun confirm(orderId: Long, items: List<ConfirmItem>)

    fun cancel(orderId: Long, items: List<CancelItem>)

    fun decrease(items: List<DecreaseItem>)

    fun getOrderIds(skuId: String): Set<Long>

    data class ReserveItem(val skuId: String, val quantity: Int)

    data class CancelItem(val skuId: String, val quantity: Int)

    data class DecreaseItem(val skuId: String, val quantity: Int)

    data class ConfirmItem(val skuId: String, val quantity: Int)

    data class AddItem(val skuId: String, val quantity: Int)
}
