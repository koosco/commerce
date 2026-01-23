package com.koosco.inventoryservice.inventory.infra.storage.primary

import com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

/**
 * fileName       : RedisInventoryStockQueryAdapter
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:55
 * description    : lua를 사용하지 않고 eventual consistency 허용
 */
@Component
class RedisInventoryStockQueryAdapter(private val redisTemplate: RedisTemplate<String, String>) :
    InventoryStockQueryPort {

    override fun getStock(skuId: String): InventoryStockQueryPort.StockView {
        val stock = getInt(stockKey(skuId))
        val reserved = getInt(reservedKey(skuId))

        return InventoryStockQueryPort.StockView(skuId, stock, reserved, stock - reserved)
    }

    override fun getStocks(skuIds: List<String>): List<InventoryStockQueryPort.StockView> {
        if (skuIds.isEmpty()) return emptyList()

        val stockKeys = skuIds.map { stockKey(it) }
        val reservedKeys = skuIds.map { reservedKey(it) }

        val stockValues = redisTemplate.opsForValue().multiGet(stockKeys).orEmpty()
        val reservedValues = redisTemplate.opsForValue().multiGet(reservedKeys).orEmpty()

        return skuIds.mapIndexed { index, skuId ->
            val stock = stockValues.getOrNull(index)?.toIntOrNull() ?: 0
            val reserved = reservedValues.getOrNull(index)?.toIntOrNull() ?: 0

            InventoryStockQueryPort.StockView(
                skuId = skuId,
                total = stock,
                reserved = reserved,
                available = stock - reserved,
            )
        }
    }

    private fun getInt(key: String): Int = redisTemplate.opsForValue().get(key)?.toInt()
        ?: throw IllegalArgumentException("Inventory not initialized. key=$key")

    private fun stockKey(skuId: String) = "inventory:stock:$skuId"

    private fun reservedKey(skuId: String) = "inventory:reserved:$skuId"
}
