package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.common.MessageContext
import com.koosco.inventoryservice.inventory.application.command.ReserveStockCommand
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderPlacedEvent
import com.koosco.inventoryservice.inventory.application.usecase.ReserveStockUseCase
import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaOrderPlacedEventConsumer
 * author         : koo
 * date           : 2025. 12. 19. 오후 12:38
 * description    : OrderCreatedEvent 처리 리스너
 */
@Component
class KafkaOrderPlacedEventConsumer(private val reserveStockUseCase: ReserveStockUseCase) {
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
            ack.acknowledge() // poison message → skip
            return
        }

        logger.info(
            "Received OrderPlacedEvent: eventId={}, correlationId={}, orderId={}",
            event.id,
            orderPlaced.correlationId,
            orderPlaced.orderId,
        )

        val context = MessageContext(
            correlationId = orderPlaced.correlationId,
            causationId = event.id,
        )

        val command = ReserveStockCommand(
            orderId = orderPlaced.orderId,
            items = orderPlaced.items.map { item ->
                ReserveStockCommand.ReservedSku(
                    skuId = item.skuId,
                    quantity = item.quantity,
                )
            },
        )

        try {
            reserveStockUseCase.execute(command, context)

            ack.acknowledge()
            logger.info(
                "Successfully reserved stock for ORDER: eventId=${event.id}, orderId=${orderPlaced.orderId}, items=${orderPlaced.items}",
            )
        } catch (_: NotEnoughStockException) {
            // 재고 부족 -> 재시도하지 않음 -> TODO : notification 처리
            logger.warn(
                "Stock reservation failed: eventId=${event.id}, orderId=${orderPlaced.orderId}",
            )
            ack.acknowledge()
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderPlacedEvent: eventId=${event.id}, orderId=${orderPlaced.orderId}",
                e,
            )
            // ❗ ack 안 함 → retry / DLQ
            throw e
        }
    }
}
