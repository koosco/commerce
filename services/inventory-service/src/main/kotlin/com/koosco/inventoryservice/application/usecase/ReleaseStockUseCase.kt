package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.application.command.CancelStockCommand
import com.koosco.inventoryservice.application.port.IntegrationEventProducer
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.outbound.inventory.StockRestoredEvent
import com.koosco.inventoryservice.domain.enums.InventoryAction
import org.slf4j.LoggerFactory

@UseCase
class ReleaseStockUseCase(
    private val inventoryStockStore: InventoryStockStorePort,
    private val inventoryStockQuery: InventoryStockQueryPort,
    private val inventoryLogPort: InventoryLogPort,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: CancelStockCommand, context: MessageContext) {
        // 취소 전 가용 재고 확인 (재고 복구 판단용)
        val stockBeforeCancel = command.items.associate { item ->
            item.skuId to try {
                inventoryStockQuery.getStock(item.skuId).available
            } catch (e: Exception) {
                null
            }
        }

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

        // 재고 복구 여부 확인 후 StockRestored 이벤트 발행
        publishStockRestoredIfNeeded(command, context, stockBeforeCancel)

        logger.info(
            "release stock for orderId=${command.orderId}, eventId=${context.correlationId}, causationId=${context.causationId}",
        )
    }

    private fun publishStockRestoredIfNeeded(
        command: CancelStockCommand,
        context: MessageContext,
        stockBeforeCancel: Map<String, Int?>,
    ) {
        command.items.forEach { item ->
            try {
                val availableBefore = stockBeforeCancel[item.skuId] ?: return@forEach
                if (availableBefore <= 0) {
                    val stockAfter = inventoryStockQuery.getStock(item.skuId)
                    if (stockAfter.available > 0) {
                        integrationEventProducer.publish(
                            StockRestoredEvent(
                                orderId = command.orderId,
                                skuId = item.skuId,
                                correlationId = context.correlationId,
                                causationId = context.causationId,
                            ),
                        )
                        logger.info(
                            "Stock restored event published. skuId={}, orderId={}",
                            item.skuId,
                            command.orderId,
                        )
                    }
                }
            } catch (e: Exception) {
                logger.warn(
                    "Failed to check stock restoration. skuId={}, orderId={}",
                    item.skuId,
                    command.orderId,
                    e,
                )
            }
        }
    }
}
