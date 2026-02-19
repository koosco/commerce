package com.koosco.inventoryservice.infra.storage.primary

import com.koosco.common.core.exception.NotFoundException
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.common.InventoryErrorCode
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component

@Component
class RedisInventoryStockAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
    private val addStockScript: DefaultRedisScript<Long>,
    private val reserveStockScript: DefaultRedisScript<Long>,
    private val confirmStockScript: DefaultRedisScript<Long>,
    private val cancelStockScript: DefaultRedisScript<Long>,
    private val decreaseStockScript: DefaultRedisScript<Long>,
) : InventoryStockStorePort {

    override fun initialize(skuId: String, initialQuantity: Int) {
        val stockKey = stockKey(skuId)
        val reservedKey = reservedKey(skuId)

        val existed = redisTemplate.hasKey(stockKey) == true
        if (existed) {
            throw IllegalStateException("Inventory already initialized. skuId=$skuId")
        }

        redisTemplate.opsForValue().set(stockKey, initialQuantity.toString())
        redisTemplate.opsForValue().set(reservedKey, "0")
    }

    override fun add(items: List<InventoryStockStorePort.AddItem>) {
        items.forEach { item ->
            val result = exec(
                addStockScript,
                listOf(stockKey(item.skuId)),
                item.quantity.toString(),
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            }
        }
    }

    override fun reserve(orderId: Long, items: List<InventoryStockStorePort.ReserveItem>) {
        items.forEach { item ->
            val result = exec(
                reserveStockScript,
                listOf(stockKey(item.skuId), reservedKey(item.skuId), ordersKey(item.skuId)),
                item.quantity.toString(),
                orderId.toString(),
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
                -2L -> throw NotEnoughStockException(
                    skuId = item.skuId,
                    requestedQuantity = item.quantity,
                )
            }
        }
    }

    override fun confirm(orderId: Long, items: List<InventoryStockStorePort.ConfirmItem>) {
        items.forEach { item ->
            val result = exec(
                confirmStockScript,
                listOf(reservedKey(item.skuId), ordersKey(item.skuId)),
                item.quantity.toString(),
                orderId.toString(),
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
                -2L -> error("Invalid confirm state. skuId=${item.skuId}")
            }
        }
    }

    override fun cancel(orderId: Long, items: List<InventoryStockStorePort.CancelItem>) {
        items.forEach { item ->
            val result = exec(
                cancelStockScript,
                listOf(stockKey(item.skuId), reservedKey(item.skuId), ordersKey(item.skuId)),
                item.quantity.toString(),
                orderId.toString(),
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
                -2L -> error("Invalid cancel state. skuId=${item.skuId}")
            }
        }
    }

    override fun decrease(items: List<InventoryStockStorePort.DecreaseItem>) {
        items.forEach { item ->
            val result = exec(
                decreaseStockScript,
                listOf(stockKey(item.skuId)),
                item.quantity.toString(),
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
                -2L -> throw NotEnoughStockException(
                    skuId = item.skuId,
                    requestedQuantity = item.quantity,
                )
            }
        }
    }

    override fun getOrderIds(skuId: String): Set<Long> {
        val members = redisTemplate.opsForSet().members(ordersKey(skuId)) ?: emptySet()
        return members.mapNotNull { it.toLongOrNull() }.toSet()
    }

    private fun exec(script: DefaultRedisScript<Long>, keys: List<String>, vararg args: String): Long =
        redisTemplate.execute(script, keys, *args)
            ?: throw IllegalStateException(
                "Redis script execution returned null. script=$script",
            )

    private fun stockKey(skuId: String) = "inventory:stock:$skuId"
    private fun reservedKey(skuId: String) = "inventory:reserved:$skuId"
    private fun ordersKey(skuId: String) = "inventory:orders:$skuId"
}
