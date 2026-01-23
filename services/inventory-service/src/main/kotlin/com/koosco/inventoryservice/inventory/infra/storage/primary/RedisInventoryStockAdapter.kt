package com.koosco.inventoryservice.inventory.infra.storage.primary

import com.koosco.common.core.exception.NotFoundException
import com.koosco.inventoryservice.common.InventoryErrorCode
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component

/**
 * fileName       : RedisInventoryStockAdapter
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:12
 * description    :
 */

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
                stockKey(item.skuId),
                null,
                item.quantity,
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            }
        }
    }

    override fun reserve(items: List<InventoryStockStorePort.ReserveItem>) {
        items.forEach { item ->
            val result = exec(
                reserveStockScript,
                stockKey(item.skuId),
                reservedKey(item.skuId),
                item.quantity,
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

    override fun confirm(items: List<InventoryStockStorePort.ConfirmItem>) {
        items.forEach { item ->
            val result = exec(
                confirmStockScript,
                reservedKey(item.skuId),
                null,
                item.quantity,
            )

            when (result) {
                -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
                -2L -> error("Invalid confirm state. skuId=${item.skuId}")
            }
        }
    }

    override fun cancel(items: List<InventoryStockStorePort.CancelItem>) {
        items.forEach { item ->
            val result = exec(
                cancelStockScript,
                stockKey(item.skuId),
                reservedKey(item.skuId),
                item.quantity,
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
                stockKey(item.skuId),
                null,
                item.quantity,
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

    private fun exec(script: DefaultRedisScript<Long>, k1: String, k2: String?, qty: Int): Long {
        val keys = listOfNotNull(k1, k2)

        return redisTemplate.execute(script, keys, qty.toString())
            ?: throw IllegalStateException(
                "Redis script execution returned null. script=$script",
            )
    }

    private fun stockKey(skuId: String) = "inventory:stock:$skuId"
    private fun reservedKey(skuId: String) = "inventory:reserved:$skuId"
}
