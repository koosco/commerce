package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.application.command.DeductStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.port.InventoryLogPort
import com.koosco.inventoryservice.application.port.InventoryRepositoryPort
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import com.koosco.inventoryservice.domain.enums.InventoryAction
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * 일반 구매 재고 차감 UseCase
 *
 * Flash Sale과 달리 Redis 예약 없이 RDB 조건부 UPDATE로 직접 차감한다.
 * UPDATE inventory SET total_stock = total_stock - :qty
 * WHERE sku_id = :skuId AND (total_stock - reserved_stock) >= :qty
 *
 * 영향받은 row 수로 성공/실패를 판단한다 (1행: 성공, 0행: 재고 부족).
 */
@UseCase
class DeductStockUseCase(
    private val inventoryRepositoryPort: InventoryRepositoryPort,
    private val inventoryLogPort: InventoryLogPort,
    private val apiIdempotencyRepository: InventoryApiIdempotencyRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: DeductStockCommand) {
        if (command.idempotencyKey != null) {
            if (apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                    command.idempotencyKey,
                    InventoryApiIdempotency.DEDUCT_STOCK,
                )
            ) {
                logger.info(
                    "Deduct stock already processed. orderId={}, idempotencyKey={}",
                    command.orderId,
                    command.idempotencyKey,
                )
                return
            }
        }

        command.items.forEach { item ->
            val updatedRows = inventoryRepositoryPort.deductQuantity(item.skuId, item.quantity)
            if (updatedRows == 0) {
                throw NotEnoughStockException(
                    message = "Not enough stock for skuId=${item.skuId}: requested=${item.quantity}",
                    skuId = item.skuId,
                    requestedQuantity = item.quantity,
                )
            }
        }

        inventoryLogPort.logBatch(
            command.items.map {
                InventoryLogPort.LogEntry(
                    skuId = it.skuId,
                    orderId = command.orderId,
                    action = InventoryAction.DEDUCT,
                    quantity = it.quantity,
                )
            },
        )

        if (command.idempotencyKey != null) {
            apiIdempotencyRepository.save(
                InventoryApiIdempotency.create(command.idempotencyKey, InventoryApiIdempotency.DEDUCT_STOCK),
            )
        }

        logger.info(
            "Stock deducted successfully. orderId={}, items={}",
            command.orderId,
            command.items.size,
        )
    }
}
