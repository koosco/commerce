package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.command.InitStockCommand
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.inventory.domain.exception.InventoryAlreadyInitialized
import org.slf4j.LoggerFactory

/**
 * 재고 초기화 유스케이스
 * 새로운 상품이 생성되었을 때 재고를 초기화합니다.
 */
@UseCase
class InitializeStockUseCase(private val inventoryStockStore: InventoryStockStorePort) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 재고 초기화 처리
     *
     * @param command 재고 초기화 정보 (skuId, initialQuantity)
     */
    fun execute(command: InitStockCommand) {
        try {
            inventoryStockStore.initialize(
                skuId = command.skuId,
                initialQuantity = command.initialQuantity,
            )
        } catch (e: InventoryAlreadyInitialized) {
            logger.warn("Inventory already exists for skuId: ${command.skuId}")
            throw e
        }
    }
}
