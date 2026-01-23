package com.koosco.inventoryservice.inventory.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.inventory.api.request.GetInventoriesRequest
import com.koosco.inventoryservice.inventory.api.response.GetInventoriesResponse
import com.koosco.inventoryservice.inventory.api.response.GetInventoryResponse
import com.koosco.inventoryservice.inventory.application.command.GetInventoriesCommand
import com.koosco.inventoryservice.inventory.application.command.GetInventoryCommand
import com.koosco.inventoryservice.inventory.application.usecase.GetInventoryUseCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inventories")
class QueryInventoryController(private val getInventoryUseCase: GetInventoryUseCase) {

    @Operation(
        summary = "재고 조회",
        description = "SKU ID로 재고 정보를 조회합니다.",
    )
    @GetMapping("/{skuId}")
    fun getInventoryBySkuId(@PathVariable skuId: String): ApiResponse<GetInventoryResponse> {
        val result = getInventoryUseCase.execute(GetInventoryCommand(skuId = skuId))

        return ApiResponse.success(
            GetInventoryResponse(
                skuId = result.skuId,
                totalStock = result.totalStock,
                reservedStock = result.reservedStock,
                availableStock = result.availableStock,
            ),
        )
    }

    @Operation(
        summary = "대량 재고 조회",
        description = "여러 SKU ID로 재고 정보를 대량 조회합니다.",
    )
    @PostMapping("/bulk")
    fun getInventoryBySkuIds(@RequestBody body: GetInventoriesRequest): ApiResponse<GetInventoriesResponse> {
        val result = getInventoryUseCase.execute(GetInventoriesCommand(skuIds = body.skuIds))

        return ApiResponse.success(
            GetInventoriesResponse(
                inventories = result.map {
                    GetInventoriesResponse.InventoryInfo(
                        it.skuId,
                        it.totalStock,
                        it.reservedStock,
                        it.availableStock,
                    )
                },
            ),
        )
    }
}
