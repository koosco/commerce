package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.messaging.MessageContext
import com.koosco.inventoryservice.inventory.application.command.CancelStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import org.slf4j.LoggerFactory

/**
 * fileName       : CancelStockUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 6:34
 * description    : 예약된 재고 취소 Usecase
 */
@UseCase
class ReleaseStockUseCase(private val inventoryStockStore: InventoryStockStorePort) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 예약 취소 처리 (결제 실패/주문 취소)
     */
    fun execute(command: CancelStockCommand, context: MessageContext) {
        inventoryStockStore.cancel(
            command.items.map {
                InventoryStockStorePort.CancelItem(
                    skuId = it.skuId,
                    quantity = it.quantity,
                )
            },
        ).also {
            logger.info(
                "release stock for orderId=${command.orderId}, eventId=${context.correlationId}, causationId=${context.causationId}",
            )
        }
    }
}
