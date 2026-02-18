package com.koosco.inventoryservice.inventory.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.inventory.api.response.GetInventoryLogsResponse
import com.koosco.inventoryservice.inventory.application.command.GetInventoryLogsCommand
import com.koosco.inventoryservice.inventory.application.usecase.GetInventoryLogsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Admin Controller", description = "관리자용 재고 관리 API")
@RestController
@RequestMapping("/api/inventories")
class AdminController(private val getInventoryLogsUseCase: GetInventoryLogsUseCase) {

    @Operation(
        summary = "기간내 재고 변경 로그 조회",
        description = "관리자용으로 기간내 재고 변경 로그를 조회합니다.",
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/logs")
    fun getInventoryChangeLogs(
        @RequestParam skuId: String,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime?,
    ): ApiResponse<GetInventoryLogsResponse> {
        val results = getInventoryLogsUseCase.execute(
            GetInventoryLogsCommand(
                skuId = skuId,
                from = from,
                to = to,
            ),
        )

        return ApiResponse.success(
            GetInventoryLogsResponse(
                logs = results.map {
                    GetInventoryLogsResponse.InventoryLogInfo(
                        id = it.id,
                        skuId = it.skuId,
                        orderId = it.orderId,
                        action = it.action,
                        quantity = it.quantity,
                        createdAt = it.createdAt,
                    )
                },
            ),
        )
    }
}
