package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.GetInventoryLogsCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryLogPort
import com.koosco.inventoryservice.inventory.application.result.GetInventoryLogsResult

@UseCase
class GetInventoryLogsUseCase(private val inventoryLogPort: InventoryLogPort) {

    fun execute(command: GetInventoryLogsCommand): List<GetInventoryLogsResult> {
        val logs = inventoryLogPort.findBySkuId(
            skuId = command.skuId,
            from = command.from,
            to = command.to,
        )

        return logs.map {
            GetInventoryLogsResult(
                id = it.id,
                skuId = it.skuId,
                orderId = it.orderId,
                action = it.action,
                quantity = it.quantity,
                createdAt = it.createdAt,
            )
        }
    }
}
