package com.koosco.inventoryservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.application.command.ReserveStockCommand
import com.koosco.inventoryservice.application.usecase.ReserveStockUseCase
import com.koosco.inventoryservice.contract.inbound.order.OrderPlacedEvent
import com.koosco.inventoryservice.domain.entity.InventoryEventIdempotency.Companion.Actions
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import com.koosco.inventoryservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * OrderPlacedEvent 처리 - 재고 예약
 */
@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val reserveStockUseCase: ReserveStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
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

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.RESERVE_STOCK)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${orderPlaced.orderId}")
            ack.acknowledge()
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

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                messageId = event.id,
                action = Actions.RESERVE_STOCK,
                aggregateId = orderPlaced.orderId.toString(),
            )

            ack.acknowledge()
            logger.info(
                "Successfully reserved stock for ORDER: eventId=${event.id}, orderId=${orderPlaced.orderId}",
            )
        } catch (_: NotEnoughStockException) {
            // 재고 부족 -> 재시도하지 않음
            logger.warn(
                "Stock reservation failed (not enough stock): eventId=${event.id}, orderId=${orderPlaced.orderId}",
            )
            ack.acknowledge()
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderPlacedEvent: eventId=${event.id}, orderId=${orderPlaced.orderId}",
                e,
            )
            throw e
        }
    }
}
