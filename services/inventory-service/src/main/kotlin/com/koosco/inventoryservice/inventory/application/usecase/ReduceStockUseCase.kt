package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.BulkReduceStockCommand
import com.koosco.inventoryservice.inventory.application.command.ReduceStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryLogPort
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction

@UseCase
class ReduceStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
) {

    fun execute(command: ReduceStockCommand) {
        inventoryStockStore.decrease(
            listOf(
                InventoryStockStorePort.DecreaseItem(
                    skuId = command.skuId,
                    quantity = command.reducingQuantity,
                ),
            ),
        )

        inventoryLogPort.log(
            skuId = command.skuId,
            orderId = null,
            action = InventoryAction.DECREASE,
            quantity = command.reducingQuantity,
        )
    }

    fun execute(command: BulkReduceStockCommand) {
        inventoryStockStore.decrease(
            command.items.map {
                InventoryStockStorePort.DecreaseItem(
                    it.skuId,
                    it.reducingQuantity,
                )
            },
        )

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = null,
                    action = InventoryAction.DECREASE,
                    quantity = it.reducingQuantity,
                )
            },
        )
    }
}
