package com.koosco.inventoryservice.infra.storage

import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import com.koosco.inventoryservice.infra.storage.secondary.JpaInventorySnapshotRepository
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Primary
@Component
class CircuitBreakerInventoryStockQueryAdapter(
    @Qualifier("redisInventoryStockQueryAdapter")
    private val redisQueryAdapter: InventoryStockQueryPort,
    private val snapshotRepository: JpaInventorySnapshotRepository,
) : InventoryStockQueryPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "getStockFallback")
    override fun getStock(skuId: String): InventoryStockQueryPort.StockView = redisQueryAdapter.getStock(skuId)

    @CircuitBreaker(name = "redis-stock", fallbackMethod = "getStocksFallback")
    override fun getStocks(skuIds: List<String>): List<InventoryStockQueryPort.StockView> =
        redisQueryAdapter.getStocks(skuIds)

    @Suppress("unused")
    private fun getStockFallback(skuId: String, ex: Throwable): InventoryStockQueryPort.StockView {
        logger.warn("Redis circuit breaker open for getStock, falling back to MariaDB snapshot. skuId={}", skuId, ex)
        val snapshot = snapshotRepository.findByIdOrNull(skuId)
            ?: throw IllegalArgumentException("Inventory snapshot not found. skuId=$skuId")
        return InventoryStockQueryPort.StockView(
            skuId = snapshot.skuId,
            total = snapshot.total,
            reserved = snapshot.reserved,
            available = snapshot.total - snapshot.reserved,
        )
    }

    @Suppress("unused")
    private fun getStocksFallback(skuIds: List<String>, ex: Throwable): List<InventoryStockQueryPort.StockView> {
        logger.warn(
            "Redis circuit breaker open for getStocks, falling back to MariaDB snapshot. skuIds={}",
            skuIds,
            ex,
        )
        val snapshots = snapshotRepository.findAllById(skuIds)
        return snapshots.map {
            InventoryStockQueryPort.StockView(
                skuId = it.skuId,
                total = it.total,
                reserved = it.reserved,
                available = it.total - it.reserved,
            )
        }
    }
}
