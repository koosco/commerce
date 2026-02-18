package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.InitStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryLogPort
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction
import com.koosco.inventoryservice.inventory.domain.exception.InventoryAlreadyInitialized
import org.slf4j.LoggerFactory

@UseCase
class InitializeStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: InitStockCommand) {
        try {
            inventoryStockStore.initialize(
                skuId = command.skuId,
                initialQuantity = command.initialQuantity,
            )
        } catch (e: InventoryAlreadyInitialized) {
            logger.warn("Inventory already exists for skuId: ${command.skuId}")
            throw e
        }

        inventoryLogPort.log(
            skuId = command.skuId,
            orderId = null,
            action = InventoryAction.INITIALIZE,
            quantity = command.initialQuantity,
        )
    }
}
