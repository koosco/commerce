package com.koosco.inventoryservice.infra.storage.primary

import com.koosco.inventoryservice.application.port.InventoryStockSnapshotQueryPort
import com.koosco.inventoryservice.application.port.InventoryStockSnapshotQueryPort.SnapshotStockView
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component

@Component
class RedisInventoryStockSnapshotQueryAdapter(private val redisTemplate: RedisTemplate<String, String>) :
    InventoryStockSnapshotQueryPort {

    override fun getAllStocks(): List<SnapshotStockView> {
        val skuIds = mutableSetOf<String>()

        redisTemplate.scan(ScanOptions.scanOptions().match(STOCK_KEY_PATTERN).count(100).build()).use { cursor ->
            cursor.forEach { key ->
                skuIds.add(key.removePrefix(STOCK_KEY_PREFIX))
            }
        }

        if (skuIds.isEmpty()) return emptyList()

        val skuIdList = skuIds.toList()
        val stockKeys = skuIdList.map { "$STOCK_KEY_PREFIX$it" }
        val reservedKeys = skuIdList.map { "$RESERVED_KEY_PREFIX$it" }

        val stockValues = redisTemplate.opsForValue().multiGet(stockKeys).orEmpty()
        val reservedValues = redisTemplate.opsForValue().multiGet(reservedKeys).orEmpty()

        return skuIdList.mapIndexed { index, skuId ->
            SnapshotStockView(
                skuId = skuId,
                total = stockValues.getOrNull(index)?.toIntOrNull() ?: 0,
                reserved = reservedValues.getOrNull(index)?.toIntOrNull() ?: 0,
            )
        }
    }

    companion object {
        private const val STOCK_KEY_PREFIX = "inventory:stock:"
        private const val RESERVED_KEY_PREFIX = "inventory:reserved:"
        private const val STOCK_KEY_PATTERN = "inventory:stock:*"
    }
}
