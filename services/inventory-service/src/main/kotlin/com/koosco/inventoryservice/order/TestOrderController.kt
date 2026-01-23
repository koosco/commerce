package com.koosco.inventoryservice.order

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderPlacedEvent
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

/**
 * fileName       : TestOrderController
 * author         : koo
 * date           : 2025. 12. 25. 오전 2:16
 * description    : 주문 이벤트 발행을 위한 테스트 컨트롤러, local 환경에서만 사용
 */
@Profile("local")
@RestController
@RequestMapping("/api/orders/test")
class TestOrderController(
    private val testOrderService: TestOrderService,
    private val testOrderConsumer: TestOrderConsumer,
) {

    // ===== 이벤트 발행 =====

    @PostMapping("/placed")
    fun orderPlaced(): ApiResponse<OrderPlacedEvent> {
        val orderPlacedEvent = testOrderService.placeOrder()
        return ApiResponse.success(orderPlacedEvent)
    }

    @PostMapping("/cancelled")
    fun orderCancelled(): ApiResponse<OrderCancelledEvent> {
        val orderCancelledEvent = testOrderService.cancelOrder()
        return ApiResponse.success(orderCancelledEvent)
    }

    @PostMapping("/confirmed")
    fun orderConfirmed(): ApiResponse<OrderConfirmedEvent> {
        val orderConfirmedEvent = testOrderService.confirmOrder()
        return ApiResponse.success(orderConfirmedEvent)
    }

    // ===== 이벤트 검증 =====

    @GetMapping("/events/{eventType}")
    fun getReceivedEvents(@PathVariable eventType: String): ApiResponse<List<Any>> {
        val events = testOrderConsumer.getReceivedEvents(eventType)
        return ApiResponse.success(events)
    }

    @GetMapping("/events")
    fun getAllReceivedEvents(): ApiResponse<Map<String, List<Any>>> {
        val allEvents = testOrderConsumer.getAllReceivedEvents()
        return ApiResponse.success(allEvents)
    }

    @DeleteMapping("/events")
    fun clearReceivedEvents(): ApiResponse<Unit> {
        testOrderConsumer.clearReceivedEvents()
        return ApiResponse.success(Unit)
    }
}
