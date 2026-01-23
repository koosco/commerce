package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.inventoryservice.common.MessageContext
import com.koosco.inventoryservice.inventory.application.command.ReserveStockCommand
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservedEvent
import com.koosco.inventoryservice.inventory.application.port.IntegrationEventPublisher
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason
import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import org.slf4j.LoggerFactory

/**
 * fileName       : ReserveStockUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 6:33
 * description    : 재고 예약 Usecase
 */
@UseCase
class ReserveStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val integrationEventPublisher: IntegrationEventPublisher,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: ReserveStockCommand, context: MessageContext) {
        try {
            inventoryStockStore.reserve(
                command.items.map {
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

        // ✅ 성공 이벤트
        integrationEventPublisher.publish(
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

        integrationEventPublisher.publish(
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
