package com.koosco.orderservice.test.payment

import com.koosco.common.core.response.ApiResponse
import com.koosco.orderservice.contract.inbound.payment.PaymentCompletedEvent
import com.koosco.orderservice.contract.inbound.payment.PaymentCreatedEvent
import com.koosco.orderservice.contract.inbound.payment.PaymentFailedEvent
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

/**
 * fileName       : PaymentTestController
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:58
 * description    : 결제 이벤트 발행 테스트를 위한 Controller, local profile only
 */
@Profile("local")
@RestController
@RequestMapping("/api/payments/test")
class TestPaymentController(
    private val testPaymentService: TestPaymentService,
    private val testPaymentConsumer: TestPaymentConsumer,
) {

    // ===== 이벤트 발행 =====

    @PostMapping("/created")
    fun created(): ApiResponse<PaymentCreatedEvent> {
        val paymentCreatedEvent = testPaymentService.created()
        return ApiResponse.success(paymentCreatedEvent)
    }

    @PostMapping("/completed")
    fun completed(): ApiResponse<PaymentCompletedEvent> {
        val paymentCompletedEvent = testPaymentService.completed()
        return ApiResponse.success(paymentCompletedEvent)
    }

    @PostMapping("/failed")
    fun failed(): ApiResponse<PaymentFailedEvent> {
        val paymentFailedEvent = testPaymentService.failed()
        return ApiResponse.success(paymentFailedEvent)
    }

    // ===== 이벤트 검증 =====

    @GetMapping("/events/{eventType}")
    fun getReceivedEvents(@PathVariable eventType: String): ApiResponse<List<Any>> {
        val events = testPaymentConsumer.getReceivedEvents(eventType)
        return ApiResponse.success(events)
    }

    @GetMapping("/events")
    fun getAllReceivedEvents(): ApiResponse<Map<String, List<Any>>> {
        val allEvents = testPaymentConsumer.getAllReceivedEvents()
        return ApiResponse.success(allEvents)
    }

    @DeleteMapping("/events")
    fun clearReceivedEvents(): ApiResponse<Unit> {
        testPaymentConsumer.clearReceivedEvents()
        return ApiResponse.success(Unit)
    }
}
