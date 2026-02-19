package com.koosco.paymentservice.order

import com.koosco.common.core.event.CloudEvent
import com.koosco.paymentservice.contract.inbound.order.OrderPlacedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * fileName       : OrderSeeder
 * author         : koo
 * date           : 2025. 12. 24. 오후 6:56
 * description    :
 */
@Profile("local")
@Component
class OrderSeeder(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,
    @Value("\${payment.topic.mappings.order.placed}")
    private val orderPlacedTopic: String,
) {
    fun seed(): OrderPlacedEvent {
        val orderPlacedEvent = OrderPlacedEvent(
            orderId = System.currentTimeMillis(), // 고유한 orderId 생성
            userId = 10011,
            payableAmount = 10000,
            items = listOf(
                OrderPlacedEvent.PlacedItem("sku-1", 1, 5000),
                OrderPlacedEvent.PlacedItem("sku-2", 1, 5000),
            ),
            correlationId = "test-correlation-${System.currentTimeMillis()}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        val event = CloudEvent.of(
            source = "payment-service.order-seeder",
            type = "order.placed",
            data = orderPlacedEvent,
        )

        kafkaTemplate.send(orderPlacedTopic, event)
        return orderPlacedEvent
    }
}
