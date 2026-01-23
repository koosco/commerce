package com.koosco.orderservice.inventory

import com.koosco.common.core.response.ApiResponse
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmFailedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReserveFailedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReservedEvent
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

/**
 * fileName       : TestInventoryController
 * author         : koo
 * date           : 2025. 12. 26. 오전 7:11
 * description    : 재고 이벤트 발행 테스트를 위한 Controller, local profile only
 */
@Profile("local")
@RestController
@RequestMapping("/api/inventories/test")
class TestInventoryController(
    private val testInventoryService: TestInventoryService,
    private val testInventoryConsumer: TestInventoryConsumer,
) {

    // ===== 이벤트 발행 =====

    @PostMapping("/reserved")
    fun stockReserved(): ApiResponse<StockReservedEvent> {
        val stockReservedEvent = testInventoryService.stockReserved()
        return ApiResponse.success(stockReservedEvent)
    }

    @PostMapping("/reservation-failed")
    fun stockReservationFailed(): ApiResponse<StockReserveFailedEvent> {
        val stockReservationFailedEvent = testInventoryService.stockReservationFailed()
        return ApiResponse.success(stockReservationFailedEvent)
    }

    @PostMapping("/confirmed")
    fun stockConfirmed(): ApiResponse<StockConfirmedEvent> {
        val stockConfirmedEvent = testInventoryService.stockConfirmed()
        return ApiResponse.success(stockConfirmedEvent)
    }

    @PostMapping("/confirm-failed")
    fun stockConfirmFailed(): ApiResponse<StockConfirmFailedEvent> {
        val stockConfirmFailedEvent = testInventoryService.stockConfirmFailed()
        return ApiResponse.success(stockConfirmFailedEvent)
    }

    // ===== 이벤트 검증 =====

    @GetMapping("/events/{eventType}")
    fun getReceivedEvents(@PathVariable eventType: String): ApiResponse<List<Any>> {
        val events = testInventoryConsumer.getReceivedEvents(eventType)
        return ApiResponse.success(events)
    }

    @GetMapping("/events")
    fun getAllReceivedEvents(): ApiResponse<Map<String, List<Any>>> {
        val allEvents = testInventoryConsumer.getAllReceivedEvents()
        return ApiResponse.success(allEvents)
    }

    @DeleteMapping("/events")
    fun clearReceivedEvents(): ApiResponse<Unit> {
        testInventoryConsumer.clearReceivedEvents()
        return ApiResponse.success(Unit)
    }
}
