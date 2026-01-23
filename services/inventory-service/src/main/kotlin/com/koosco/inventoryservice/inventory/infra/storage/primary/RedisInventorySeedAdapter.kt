package com.koosco.inventoryservice.inventory.infra.storage.primary

import com.koosco.inventoryservice.inventory.application.port.InventorySeedPort
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * fileName       : RedisInventorySeedAdapter
 * author         : koo
 * date           : 2025. 12. 30. 오전 12:15
 * description    :
 */
@Profile("local")
@Component
class RedisInventorySeedAdapter(private val redisTemplate: RedisTemplate<String, String>) : InventorySeedPort {

    private val stockKeySet = ConcurrentHashMap.newKeySet<String>()

    override fun initStock(skuId: String, initialQuantity: Int) {
        // 전체 재고 초기화
        redisTemplate.opsForValue().set(stockKey(skuId), initialQuantity.toString())

        // 예약 재고 초기화
        redisTemplate.opsForValue().set(reservedKey(skuId), "0")

        stockKeySet.add(stockKey(skuId))
    }

    override fun initStocks(skuIds: List<String>, initialQuantity: Int) {
        if (skuIds.isEmpty()) return

        val ops = redisTemplate.opsForValue()

        skuIds.forEach {
            ops.set(stockKey(it), initialQuantity.toString())
            ops.set(reservedKey(it), "0")

            stockKeySet.add(stockKey(it))
        }
    }

    override fun clear() {
        stockKeySet.forEach { redisTemplate.delete(it) }
        stockKeySet.clear()
    }

    private fun stockKey(skuId: String) = "inventory:stock:$skuId"

    private fun reservedKey(skuId: String) = "inventory:reserved:$skuId"
}
