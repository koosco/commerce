package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.ConfirmStockCommand
import com.koosco.inventoryservice.application.port.IntegrationEventProducer
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.domain.enums.InventoryAction
import com.koosco.inventoryservice.domain.enums.StockConfirmFailReason
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.slf4j.LoggerFactory

@UseCase
class ConfirmStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryLogPort: InventoryLogPort,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: ConfirmStockCommand, context: MessageContext) {
        try {
            inventoryStockStore.confirm(
                orderId = command.orderId,
                items = command.items.map {
                    InventoryStockStorePort.ConfirmItem(
                        skuId = it.skuId,
                        quantity = it.quantity,
                    )
                },
            )
        } catch (e: NotFoundException) {
            publishFailed(command, context, StockConfirmFailReason.SKU_NOT_FOUND)
            throw e
        } catch (e: NotEnoughStockException) {
            publishFailed(command, context, StockConfirmFailReason.NOT_ENOUGH_RESERVED)
            throw e
        }

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = command.orderId,
                    action = InventoryAction.CONFIRM,
                    quantity = it.quantity,
                )
            },
        )

        integrationEventProducer.publish(
            StockConfirmedEvent(
                orderId = command.orderId,
                items = command.items.map {
                    StockConfirmedEvent.ConfirmedItem(it.skuId, it.quantity)
                },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )

        logger.info(
            "Stock confirmed successfully. orderId={}, items={}",
            command.orderId,
            command.items.size,
        )
    }

    private fun publishFailed(command: ConfirmStockCommand, context: MessageContext, reason: StockConfirmFailReason) {
        integrationEventProducer.publish(
            StockConfirmFailedEvent(
                orderId = command.orderId,
                reason = reason,
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        ).also {
            logger.warn(
                "Stock confirmation failed. orderId=${command.orderId}, reason=$reason, correlationId=${context.correlationId}",
            )
        }
    }
}
