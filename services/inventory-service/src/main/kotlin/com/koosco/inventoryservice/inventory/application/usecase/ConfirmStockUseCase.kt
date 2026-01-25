package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.inventory.application.command.ConfirmStockCommand
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.inventory.application.port.IntegrationEventPublisher
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason
import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import org.slf4j.LoggerFactory

@UseCase
class ConfirmStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val integrationEventPublisher: IntegrationEventPublisher,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: ConfirmStockCommand, context: MessageContext) {
        try {
            inventoryStockStore.confirm(
                command.items.map {
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
            // 예약 부족/상태 이상을 NotEnoughStock으로 매핑했다는 전제
            publishFailed(command, context, StockConfirmFailReason.NOT_ENOUGH_RESERVED)
            throw e
        }

        integrationEventPublisher.publish(
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
        integrationEventPublisher.publish(
            StockConfirmFailedEvent(
                orderId = command.orderId,
                reason = reason, // enum 권장
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
