package com.koosco.inventoryservice.infra.persistence

import com.koosco.inventoryservice.domain.entity.InventoryLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface InventoryLogRepository : JpaRepository<InventoryLog, Long> {

    fun findBySkuIdOrderByCreatedAtDesc(skuId: String): List<InventoryLog>

    @Query(
        "SELECT l FROM InventoryLog l WHERE l.skuId = :skuId " +
            "AND l.createdAt >= :from AND l.createdAt <= :to " +
            "ORDER BY l.createdAt DESC",
    )
    fun findBySkuIdAndPeriod(skuId: String, from: LocalDateTime, to: LocalDateTime): List<InventoryLog>

    @Query(
        "SELECT l FROM InventoryLog l WHERE l.skuId = :skuId " +
            "AND l.createdAt >= :from ORDER BY l.createdAt DESC",
    )
    fun findBySkuIdAndFrom(skuId: String, from: LocalDateTime): List<InventoryLog>

    @Query(
        "SELECT l FROM InventoryLog l WHERE l.skuId = :skuId " +
            "AND l.createdAt <= :to ORDER BY l.createdAt DESC",
    )
    fun findBySkuIdAndTo(skuId: String, to: LocalDateTime): List<InventoryLog>

    fun findByOrderId(orderId: Long): List<InventoryLog>
}
