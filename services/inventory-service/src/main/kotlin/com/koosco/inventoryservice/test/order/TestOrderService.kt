package com.koosco.inventoryservice.test.order

import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.application.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.application.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.application.contract.inbound.order.OrderPlacedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * fileName       : TestOrderService
 * author         : koo
 * date           : 2025. 12. 25. 오전 2:14
 * description    : 주문 이벤트 발행 테스트를 위한 서비스 클래스, local 환경에서만 사용
 */
@Profile("local")
@Service
class TestOrderService(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,

    @Value("\${inventory.topic.mappings.order.placed}")
    private val orderPlacedTopic: String,

    @Value("\${inventory.topic.mappings.order.cancelled}")
    private val orderCancelledTopic: String,

    @Value("\${inventory.topic.mappings.order.confirmed}")
    private val orderConfirmedTopic: String,
) {
    companion object {
        const val ORDER_ID = 11111111111
        const val USER_ID = 11111111111
        const val FIRST_SKU_ID = "00001f4c-a36c-4a70-9347-413ce52d5d61"
        const val SECOND_SKU_ID = "0000298f-0c73-4df1-8576-ac232687c290"
        const val INITIAL_STOCK = 10000
    }

    fun placeOrder(): OrderPlacedEvent {
        val orderPlacedEvent = OrderPlacedEvent(
            orderId = ORDER_ID, // 고유한 orderId 생성
            userId = USER_ID,
            payableAmount = 10000,
            items = listOf(
                OrderPlacedEvent.PlacedItem(FIRST_SKU_ID, 10),
                OrderPlacedEvent.PlacedItem(SECOND_SKU_ID, 10),
            ),
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        val event = CloudEvent.of(
            source = "payment-service.test-order",
            type = "order.placed",
            data = orderPlacedEvent,
        )

        kafkaTemplate.send(orderPlacedTopic, event)
        return orderPlacedEvent
    }

    fun cancelOrder(): OrderCancelledEvent {
        val orderCancelledEvent = OrderCancelledEvent(
            orderId = ORDER_ID,
            reason = "TEST_CANCELLED",
            items = listOf(
                OrderCancelledEvent.CancelledItem(FIRST_SKU_ID, 10),
                OrderCancelledEvent.CancelledItem(SECOND_SKU_ID, 10),
            ),
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        val event = CloudEvent.of(
            source = "payment-service.test-order",
            type = "order.cancelled",
            data = orderCancelledEvent,
        )

        kafkaTemplate.send(orderCancelledTopic, event)
        return orderCancelledEvent
    }

    fun confirmOrder(): OrderConfirmedEvent {
        val orderConfirmedEvent = OrderConfirmedEvent(
            orderId = ORDER_ID,
            items = listOf(
                OrderConfirmedEvent.ConfirmedItem(FIRST_SKU_ID, 10),
                OrderConfirmedEvent.ConfirmedItem(SECOND_SKU_ID, 10),
            ),
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        val event = CloudEvent.of(
            source = "payment-service.test-order",
            type = "order.confirmed",
            data = orderConfirmedEvent,
        )

        kafkaTemplate.send(orderConfirmedTopic, event)
        return orderConfirmedEvent
    }
}
