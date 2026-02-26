package com.koosco.inventoryservice.infra.storage

import com.koosco.common.core.exception.NotFoundException
import com.koosco.inventoryservice.application.port.InventoryRepositoryPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.common.InventoryErrorCode
import com.koosco.inventoryservice.domain.entity.Inventory
import com.koosco.inventoryservice.domain.vo.Stock
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Primary
@Component
class CircuitBreakerInventoryStockStoreAdapter(
    @Qualifier("redisInventoryStockAdapter")
    private val redisStockAdapter: InventoryStockStorePort,
    private val inventoryRepository: InventoryRepositoryPort,
) : InventoryStockStorePort {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "initializeFallback")
    override fun initialize(skuId: String, initialQuantity: Int) = redisStockAdapter.initialize(skuId, initialQuantity)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "addFallback")
    override fun add(items: List<InventoryStockStorePort.AddItem>) = redisStockAdapter.add(items)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "reserveFallback")
    override fun reserve(orderId: Long, items: List<InventoryStockStorePort.ReserveItem>) =
        redisStockAdapter.reserve(orderId, items)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "confirmFallback")
    override fun confirm(orderId: Long, items: List<InventoryStockStorePort.ConfirmItem>) =
        redisStockAdapter.confirm(orderId, items)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "cancelFallback")
    override fun cancel(orderId: Long, items: List<InventoryStockStorePort.CancelItem>) =
        redisStockAdapter.cancel(orderId, items)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "decreaseFallback")
    override fun decrease(items: List<InventoryStockStorePort.DecreaseItem>) = redisStockAdapter.decrease(items)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "getOrderIdsFallback")
    override fun getOrderIds(skuId: String): Set<Long> = redisStockAdapter.getOrderIds(skuId)

    @Suppress("unused")
    @Transactional
    private fun initializeFallback(skuId: String, initialQuantity: Int, ex: Throwable) {
        logger.warn("Redis circuit breaker open for initialize, falling back to MariaDB. skuId={}", skuId, ex)
        val existing = inventoryRepository.findBySkuIdOrNull(skuId)
        if (existing != null) {
            throw IllegalStateException("Inventory already initialized. skuId=$skuId")
        }
        inventoryRepository.save(
            Inventory(skuId = skuId, stock = Stock(total = initialQuantity, reserved = 0)),
        )
    }

    @Suppress("unused")
    @Transactional
    private fun addFallback(items: List<InventoryStockStorePort.AddItem>, ex: Throwable) {
        logger.warn("Redis circuit breaker open for add, falling back to MariaDB", ex)
        val skuIds = items.map { it.skuId }
        val inventories = inventoryRepository.findAllBySkuIdInWithLock(skuIds).associateBy { it.skuId }
        items.forEach { item ->
            val inventory = inventories[item.skuId]
                ?: throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            inventory.increase(item.quantity)
            inventoryRepository.save(inventory)
        }
    }

    @Suppress("unused")
    @Transactional
    private fun reserveFallback(orderId: Long, items: List<InventoryStockStorePort.ReserveItem>, ex: Throwable) {
        logger.warn("Redis circuit breaker open for reserve, falling back to MariaDB. orderId={}", orderId, ex)
        val skuIds = items.map { it.skuId }
        val inventories = inventoryRepository.findAllBySkuIdInWithLock(skuIds).associateBy { it.skuId }
        items.forEach { item ->
            val inventory = inventories[item.skuId]
                ?: throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            inventory.reserve(item.quantity)
            inventoryRepository.save(inventory)
        }
    }

    @Suppress("unused")
    @Transactional
    private fun confirmFallback(orderId: Long, items: List<InventoryStockStorePort.ConfirmItem>, ex: Throwable) {
        logger.warn("Redis circuit breaker open for confirm, falling back to MariaDB. orderId={}", orderId, ex)
        val skuIds = items.map { it.skuId }
        val inventories = inventoryRepository.findAllBySkuIdInWithLock(skuIds).associateBy { it.skuId }
        items.forEach { item ->
            val inventory = inventories[item.skuId]
                ?: throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            inventory.confirm(item.quantity)
            inventoryRepository.save(inventory)
        }
    }

    @Suppress("unused")
    @Transactional
    private fun cancelFallback(orderId: Long, items: List<InventoryStockStorePort.CancelItem>, ex: Throwable) {
        logger.warn("Redis circuit breaker open for cancel, falling back to MariaDB. orderId={}", orderId, ex)
        val skuIds = items.map { it.skuId }
        val inventories = inventoryRepository.findAllBySkuIdInWithLock(skuIds).associateBy { it.skuId }
        items.forEach { item ->
            val inventory = inventories[item.skuId]
                ?: throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            inventory.cancelReservation(item.quantity)
            inventoryRepository.save(inventory)
        }
    }

    @Suppress("unused")
    @Transactional
    private fun decreaseFallback(items: List<InventoryStockStorePort.DecreaseItem>, ex: Throwable) {
        logger.warn("Redis circuit breaker open for decrease, falling back to MariaDB", ex)
        val skuIds = items.map { it.skuId }
        val inventories = inventoryRepository.findAllBySkuIdInWithLock(skuIds).associateBy { it.skuId }
        items.forEach { item ->
            val inventory = inventories[item.skuId]
                ?: throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            inventory.decrease(item.quantity)
            inventoryRepository.save(inventory)
        }
    }

    @Suppress("unused")
    private fun getOrderIdsFallback(skuId: String, ex: Throwable): Set<Long> {
        logger.warn(
            "Redis circuit breaker open for getOrderIds, returning empty set. skuId={}",
            skuId,
            ex,
        )
        return emptySet()
    }
}
