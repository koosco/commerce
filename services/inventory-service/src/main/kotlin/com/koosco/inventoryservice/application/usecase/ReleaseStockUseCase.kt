package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.CancelStockCommand
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.domain.enums.InventoryAction
import org.slf4j.LoggerFactory

@UseCase
class ReleaseStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: CancelStockCommand, context: MessageContext) {
        inventoryStockStore.cancel(
            orderId = command.orderId,
            items = command.items.map {
                InventoryStockStorePort.CancelItem(
                    skuId = it.skuId,
                    quantity = it.quantity,
                )
            },
        )

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = command.orderId,
                    action = InventoryAction.CANCEL,
                    quantity = it.quantity,
                )
            },
        )

        logger.info(
            "release stock for orderId=${command.orderId}, eventId=${context.correlationId}, causationId=${context.causationId}",
        )
    }
}
