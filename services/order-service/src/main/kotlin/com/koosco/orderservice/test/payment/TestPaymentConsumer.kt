package com.koosco.orderservice.test.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
import com.koosco.orderservice.contract.outbound.order.OrderCancelledEvent
import com.koosco.orderservice.contract.outbound.order.OrderConfirmedEvent
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * fileName       : TestPaymentConsumer
 * author         : koo
 * date           : 2025. 12. 26. 오전 7:30
 * description    : 결제 이벤트 발행 테스트를 위한 Consumer, local profile only
 */
@Profile("local")
@Component
class TestPaymentConsumer(private val objectMapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 수신한 이벤트를 메모리에 저장 (테스트 검증용)
    private val receivedEvents = ConcurrentHashMap<String, MutableList<Any>>()

    @KafkaListener(
        topics = ["\${order.topic.mappings.order.placed}"],
        groupId = "order-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onOrderPlaced(event: CloudEvent<*>) {
        logger.info("✅ [TEST] OrderPlaced received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val orderPlacedEvent = objectMapper.convertValue(payload, OrderPlacedEvent::class.java)
                logger.info("  → orderId=${orderPlacedEvent.orderId}, userId=${orderPlacedEvent.userId}")
                addEvent("order.placed", orderPlacedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize OrderPlacedEvent: eventId=${event.id}", e)
            }
        }
    }

    @KafkaListener(
        topics = ["\${order.topic.mappings.order.cancelled}"],
        groupId = "order-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onOrderCancelled(event: CloudEvent<*>) {
        logger.warn("❌ [TEST] OrderCancelled received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val orderCancelledEvent = objectMapper.convertValue(payload, OrderCancelledEvent::class.java)
                logger.warn("  → orderId=${orderCancelledEvent.orderId}, reason=${orderCancelledEvent.reason}")
                addEvent("order.cancelled", orderCancelledEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize OrderCancelledEvent: eventId=${event.id}", e)
            }
        }
    }

    @KafkaListener(
        topics = ["\${order.topic.mappings.order.confirmed}"],
        groupId = "order-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onOrderConfirmed(event: CloudEvent<*>) {
        logger.info("✅ [TEST] OrderConfirmed received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val orderConfirmedEvent = objectMapper.convertValue(payload, OrderConfirmedEvent::class.java)
                logger.info("  → orderId=${orderConfirmedEvent.orderId}, items=${orderConfirmedEvent.items}")
                addEvent("order.confirmed", orderConfirmedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize OrderConfirmedEvent: eventId=${event.id}", e)
            }
        }
    }

    private fun addEvent(eventType: String, event: Any) {
        receivedEvents.computeIfAbsent(eventType) { mutableListOf() }.add(event)
    }

    /**
     * 수신한 이벤트 조회 (테스트 검증용)
     */
    fun getReceivedEvents(eventType: String): List<Any> = receivedEvents[eventType]?.toList() ?: emptyList()

    /**
     * 모든 수신 이벤트 조회
     */
    fun getAllReceivedEvents(): Map<String, List<Any>> = receivedEvents.mapValues { it.value.toList() }

    /**
     * 수신한 이벤트 초기화 (테스트 간 독립성 보장)
     */
    fun clearReceivedEvents() {
        receivedEvents.clear()
        logger.info("🧹 [TEST] All received events cleared")
    }
}
