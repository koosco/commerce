package com.koosco.inventoryservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.contract.inbound.order.OrderPlacedEvent
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * OrderPlacedEvent 처리 - 알림 목적
 *
 * 재고 예약은 order-service에서 동기 REST 호출로 처리되므로,
 * 이 Consumer는 이벤트 수신 로그만 기록한다.
 */
@Component
@Validated
class KafkaOrderPlacedEventConsumer {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderPlacedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderPlaced = try {
            objectMapper.convertValue(payload, OrderPlacedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderPlacedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message -> skip
            return
        }

        logger.info(
            "Received OrderPlacedEvent (reservation already done via sync REST): " +
                "eventId={}, correlationId={}, orderId={}",
            event.id,
            orderPlaced.correlationId,
            orderPlaced.orderId,
        )

        ack.acknowledge()
    }
}
