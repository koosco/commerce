package com.koosco.inventoryservice.inventory.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.inventory.application.command.ReduceStockCommand
import com.koosco.inventoryservice.inventory.application.usecase.InventorySeedUseCase
import com.koosco.inventoryservice.inventory.application.usecase.ReduceStockUseCase
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * fileName       : TestInventoryStockController
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:58
 * description    : 테스트용 재고 초기화 컨트롤러 (local 환경에서만 사용)
 */
@Profile("local")
@RestController
@RequestMapping("/api/inventories/test")
class TestInventoryStockController(
    private val inventorySeedUseCase: InventorySeedUseCase,
    private val reduceStockUseCase: ReduceStockUseCase,
) {

    @PostMapping("/init")
    fun initStock() {
        inventorySeedUseCase.init()
    }

    @PostMapping("/clear")
    fun clearStock() {
        inventorySeedUseCase.clear()
    }

    @PostMapping("/decrease/{skuId}")
    fun decreaseStock(@PathVariable("skuId") skuId: String, @RequestBody request: StockDto): ApiResponse<Any> {
        reduceStockUseCase.execute(ReduceStockCommand(skuId, request.quantity))

        return ApiResponse.success()
    }

    data class StockDto(val quantity: Int)
}
