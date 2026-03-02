package com.koosco.inventoryservice.api.controller

import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.api.request.BulkAddStockRequest
import com.koosco.inventoryservice.api.request.BulkReduceStockRequest
import com.koosco.inventoryservice.api.request.ReserveStockRequest
import com.koosco.inventoryservice.application.command.BulkAddStockCommand
import com.koosco.inventoryservice.application.command.BulkReduceStockCommand
import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.application.usecase.AddStockUseCase
import com.koosco.inventoryservice.application.usecase.ReduceStockUseCase
import com.koosco.inventoryservice.application.usecase.ReserveStockUseCase
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(name = "Inventory Internal Stock Controller", description = "내부 재고 관리 API")
@RestController
@RequestMapping("/internal/inventories")
class InventoryStockController(
    private val addStockUseCase: AddStockUseCase,
    private val reduceStockUseCase: ReduceStockUseCase,
    private val reserveStockUseCase: ReserveStockUseCase,
    private val apiIdempotencyRepository: InventoryApiIdempotencyRepository,
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
                idempotencyKey = body.idempotencyKey,
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
                items = body.items.map {
                    BulkReduceStockCommand.ReducingStockInfo(
                        it.skuId,
                        it.quantity,
                    )
                },
                idempotencyKey = body.idempotencyKey,
            ),
        )

        return ApiResponse.success()
    }

    @Operation(
        summary = "주문 재고 예약 (내부)",
        description = "주문 생성 단계에서 재고를 동기 예약합니다. 내부 서비스 간 호출 전용 API입니다.",
    )
    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun reserveStock(@RequestBody body: ReserveStockRequest): ApiResponse<Any> {
        if (body.idempotencyKey != null &&
            apiIdempotencyRepository.existsByIdempotencyKeyAndOperationType(
                body.idempotencyKey,
                InventoryApiIdempotency.RESERVE_STOCK,
            )
        ) {
            return ApiResponse.success()
        }

        val context = MessageContext(
            correlationId = body.correlationId ?: "order-${body.orderId}",
            causationId = body.idempotencyKey,
        )

        reserveStockUseCase.execute(
            command = body.toCommand(),
            context = context,
            publishEvents = false,
        )

        if (body.idempotencyKey != null) {
            apiIdempotencyRepository.save(
                InventoryApiIdempotency.create(
                    idempotencyKey = body.idempotencyKey,
                    operationType = InventoryApiIdempotency.RESERVE_STOCK,
                ),
            )
        }

        return ApiResponse.success()
    }
}
