package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.ReserveStockCommand
import com.koosco.inventoryservice.application.port.IntegrationEventProducer
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservedEvent
import com.koosco.inventoryservice.domain.enums.InventoryAction
import com.koosco.inventoryservice.domain.enums.StockReservationFailReason
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.slf4j.LoggerFactory

@UseCase
class ReserveStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: ReserveStockCommand, context: MessageContext) {
        try {
            inventoryStockStore.reserve(
                orderId = command.orderId,
                items = command.items.map {
                    InventoryStockStorePort.ReserveItem(
                        skuId = it.skuId,
                        quantity = it.quantity,
                    )
                },
            )
        } catch (e: NotFoundException) {
            publishFailed(command, context, StockReservationFailReason.SKU_NOT_FOUND)
            throw e
        } catch (e: NotEnoughStockException) {
            publishFailed(command, context, StockReservationFailReason.NOT_ENOUGH_STOCK)
            throw e
        }

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = command.orderId,
                    action = InventoryAction.RESERVE,
                    quantity = it.quantity,
                )
            },
        )

        integrationEventProducer.publish(
            StockReservedEvent(
                orderId = command.orderId,
                items = command.items.map {
                    StockReservedEvent.Item(it.skuId, it.quantity)
                },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )

        logger.info(
            "Stock reserved successfully. orderId={}, items={}",
            command.orderId,
            command.items.size,
        )
    }

    private fun publishFailed(
        command: ReserveStockCommand,
        context: MessageContext,
        reason: StockReservationFailReason,
    ) {
        val failedItems = command.items.map {
            StockReservationFailedEvent.FailedItem(
                skuId = it.skuId,
                requestedQuantity = it.quantity,
                availableQuantity = null,
            )
        }

        integrationEventProducer.publish(
            StockReservationFailedEvent(
                orderId = command.orderId,
                reason = reason,
                failedItems = failedItems,
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        ).also {
            logger.warn("Stock reservation failed. orderId=${command.orderId}, reason=$reason")
        }
    }
}
