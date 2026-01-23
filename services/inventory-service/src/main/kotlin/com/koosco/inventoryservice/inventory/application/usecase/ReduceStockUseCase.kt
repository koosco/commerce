package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.BulkReduceStockCommand
import com.koosco.inventoryservice.inventory.application.command.ReduceStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort

@UseCase
class ReduceStockUseCase(private val inventoryStockStore: InventoryStockStorePort) {

    fun execute(command: ReduceStockCommand) {
        inventoryStockStore.decrease(
            listOf(
                InventoryStockStorePort.DecreaseItem(
                    skuId = command.skuId,
                    quantity = command.reducingQuantity,
                ),
            ),
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
    }
}
