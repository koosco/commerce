package com.koosco.inventoryservice.inventory.infra.persistence

import com.koosco.inventoryservice.inventory.application.port.InventoryLogPort
import com.koosco.inventoryservice.inventory.domain.entity.InventoryLog
import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class InventoryLogAdapter(private val inventoryLogRepository: InventoryLogRepository) : InventoryLogPort {

    override fun log(skuId: String, orderId: Long?, action: InventoryAction, quantity: Int) {
        inventoryLogRepository.save(
            InventoryLog(
                skuId = skuId,
                orderId = orderId,
                action = action,
                quantity = quantity,
            ),
        )
    }

    override fun logBatch(entries: List<InventoryLogPort.LogEntry>) {
        val logs = entries.map { entry ->
            InventoryLog(
                skuId = entry.skuId,
                orderId = entry.orderId,
                action = entry.action,
                quantity = entry.quantity,
            )
        }
        inventoryLogRepository.saveAll(logs)
    }

    override fun findBySkuId(skuId: String, from: LocalDateTime?, to: LocalDateTime?): List<InventoryLogPort.LogView> {
        val logs = when {
            from != null && to != null ->
                inventoryLogRepository.findBySkuIdAndPeriod(skuId, from, to)
            from != null ->
                inventoryLogRepository.findBySkuIdAndFrom(skuId, from)
            to != null ->
                inventoryLogRepository.findBySkuIdAndTo(skuId, to)
            else ->
                inventoryLogRepository.findBySkuIdOrderByCreatedAtDesc(skuId)
        }

        return logs.map { it.toLogView() }
    }

    private fun InventoryLog.toLogView() = InventoryLogPort.LogView(
        id = this.id,
        skuId = this.skuId,
        orderId = this.orderId,
        action = this.action,
        quantity = this.quantity,
        createdAt = this.createdAt,
    )
}
