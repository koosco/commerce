package com.koosco.inventoryservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.application.command.GetInventoriesCommand
import com.koosco.inventoryservice.application.command.GetInventoryCommand
import com.koosco.inventoryservice.application.port.InventoryStockQueryPort
import com.koosco.inventoryservice.application.result.GetInventoryResult

@UseCase
class GetInventoryUseCase(private val inventoryStockQuery: InventoryStockQueryPort) {

    fun execute(command: GetInventoryCommand): GetInventoryResult {
        val stock = inventoryStockQuery.getStock(command.skuId)

        return GetInventoryResult(
            skuId = stock.skuId,
            totalStock = stock.total,
            reservedStock = stock.reserved,
            availableStock = stock.available,
        )
    }

    fun execute(command: GetInventoriesCommand): List<GetInventoryResult> =
        inventoryStockQuery.getStocks(command.skuIds).map {
            GetInventoryResult(
                skuId = it.skuId,
                totalStock = it.total,
                reservedStock = it.reserved,
                availableStock = it.available,
            )
        }
}
