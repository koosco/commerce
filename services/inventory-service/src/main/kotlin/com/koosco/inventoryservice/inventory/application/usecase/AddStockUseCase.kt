package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.AddStockCommand
import com.koosco.inventoryservice.inventory.application.command.BulkAddStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort

@UseCase
class AddStockUseCase(private val inventoryStockStore: InventoryStockStorePort) {

    fun execute(command: AddStockCommand) {
        inventoryStockStore.add(
            listOf(
                InventoryStockStorePort.AddItem(
                    skuId = command.skuId,
                    quantity = command.addingQuantity,
                ),
            ),
        )
    }

    fun execute(command: BulkAddStockCommand) {
        inventoryStockStore.add(
            command.items.map {
                InventoryStockStorePort.AddItem(
                    skuId = it.skuId,
                    quantity = it.addingQuantity,
                )
            },
        )
    }
}
