package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.application.command.BulkAddStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import com.koosco.inventoryservice.domain.enums.InventoryAction

@UseCase
class AddStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
    private val apiIdempotencyRepository: InventoryApiIdempotencyRepository,
) {

    fun execute(command: BulkAddStockCommand) {
        if (command.idempotencyKey != null) {
            if (apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    command.idempotencyKey,
                    InventoryApiIdempotency.ADD_STOCK,
                )
            ) {
                return
            }
        }

        inventoryStockStore.add(
            command.items.map {
                InventoryStockStorePort.AddItem(
                    skuId = it.skuId,
                    quantity = it.addingQuantity,
                )
            },
        )

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = null,
                    action = InventoryAction.ADD,
                    quantity = it.addingQuantity,
                )
            },
        )

        if (command.idempotencyKey != null) {
            apiIdempotencyRepository.save(
                InventoryApiIdempotency.create(command.idempotencyKey, InventoryApiIdempotency.ADD_STOCK),
            )
        }
    }
}
