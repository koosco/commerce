package com.koosco.inventoryservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.api.request.BulkAddStockRequest
import com.koosco.inventoryservice.api.request.BulkReduceStockRequest
import com.koosco.inventoryservice.application.command.BulkAddStockCommand
import com.koosco.inventoryservice.application.command.BulkReduceStockCommand
import com.koosco.inventoryservice.application.usecase.AddStockUseCase
import com.koosco.inventoryservice.application.usecase.ReduceStockUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Inventory Stock Controller", description = "재고 관리 API")
@RestController
@RequestMapping("/api/inventories")
class InventoryStockController(
    private val addStockUseCase: AddStockUseCase,
    private val reduceStockUseCase: ReduceStockUseCase,
) {

    @Operation(
        summary = "대량 재고 추가",
        description = "여러 SKU ID로 재고를 대량 추가합니다.",
    )
    @PostMapping("/increase")
    fun addBulkInventories(@RequestBody body: BulkAddStockRequest): ApiResponse<Any> {
        addStockUseCase.execute(
            BulkAddStockCommand(
                items = body.items.map {
                    BulkAddStockCommand.AddingStockInfo(
                        it.skuId,
                        it.quantity,
                    )
                },
            ),
        )

        return ApiResponse.success()
    }

    @Operation(
        summary = "대량 재고 감소",
        description = "SKU ID로 재고를 대량으로 감소합니다.",
    )
    @PostMapping("/decrease")
    fun reduceBulkInventories(@RequestBody body: BulkReduceStockRequest): ApiResponse<Any> {
        reduceStockUseCase.execute(
            BulkReduceStockCommand(
                body.items.map {
                    BulkReduceStockCommand.ReducingStockInfo(
                        it.skuId,
                        it.quantity,
                    )
                },
            ),
        )

        return ApiResponse.success()
    }
}
