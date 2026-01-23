package com.koosco.paymentservice.order

import com.koosco.common.core.response.ApiResponse
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * fileName       : OrderTestController
 * author         : koo
 * date           : 2025. 12. 24. 오후 7:08
 * description    : 로컬 환경에서 결제 테스트를 위한 Order 데이터를 생성하는 컨트롤러
 *                  OrderSeeder를 통해 Kafka 이벤트를 발행하여 Payment 객체를 초기화합니다.
 */
@Profile("local")
@RestController
@RequestMapping("/test/orders")
class OrderTestController(private val orderSeeder: OrderSeeder) {

    @PostMapping("/seed")
    fun seed(): ApiResponse<OrderResponse> {
        val orderPlacedEvent = orderSeeder.seed()

        return ApiResponse.success(
            data = OrderResponse(
                orderId = orderPlacedEvent.orderId,
                userId = orderPlacedEvent.userId,
                payableAmount = orderPlacedEvent.payableAmount,
                itemCount = orderPlacedEvent.items.size,
                correlationId = orderPlacedEvent.correlationId,
                message = "Order placed event has been published to Kafka. Payment will be created automatically.",
            ),
        )
    }
}

data class OrderResponse(
    val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    val itemCount: Int,
    val correlationId: String,
    val message: String,
)
