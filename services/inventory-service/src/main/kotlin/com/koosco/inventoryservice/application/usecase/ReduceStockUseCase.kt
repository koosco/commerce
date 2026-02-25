package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.application.command.BulkReduceStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import com.koosco.inventoryservice.domain.enums.InventoryAction

@UseCase
class ReduceStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
    private val apiIdempotencyRepository: InventoryApiIdempotencyRepository,
) {

    fun execute(command: BulkReduceStockCommand) {
        if (command.idempotencyKey != null) {
            if (apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    command.idempotencyKey,
                    InventoryApiIdempotency.DECREASE_STOCK,
                )
            ) {
                return
            }
        }

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

        if (command.idempotencyKey != null) {
            apiIdempotencyRepository.save(
                InventoryApiIdempotency.create(command.idempotencyKey, InventoryApiIdempotency.DECREASE_STOCK),
            )
        }
    }
}
