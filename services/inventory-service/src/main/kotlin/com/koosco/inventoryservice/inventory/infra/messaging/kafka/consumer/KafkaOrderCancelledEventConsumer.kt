package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.inventory.application.command.CancelStockCommand
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.inventory.application.usecase.ReleaseStockUseCase
import com.koosco.inventoryservice.inventory.domain.entity.InventoryEventIdempotency.Companion.Actions
import com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason.Companion.mapCancelReason
import com.koosco.inventoryservice.inventory.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * OrderCancelledEvent 처리 - 재고 해제
 */
@Component
@Validated
class KafkaOrderCancelledEventConsumer(
    private val releaseStockUseCase: ReleaseStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.order.cancelled}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderCancelled(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderCancelledEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderCancelled = try {
            objectMapper.convertValue(payload, OrderCancelledEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderCancelledEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.RELEASE_STOCK)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${orderCancelled.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received OrderCancelledEvent: eventId=${event.id}, orderId=${orderCancelled.orderId}, reason=${orderCancelled.reason}",
        )

        val context = MessageContext(
            correlationId = orderCancelled.correlationId,
            causationId = event.id,
        )

        val command = CancelStockCommand(
            orderId = orderCancelled.orderId,
            items = orderCancelled.items.map { item ->
                CancelStockCommand.CancelledSku(
                    skuId = item.skuId,
                    quantity = item.quantity,
                )
            },
            reason = mapCancelReason(orderCancelled.reason),
        )

        try {
            releaseStockUseCase.execute(command, context)

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.RELEASE_STOCK,
                referenceId = orderCancelled.orderId.toString(),
            )

            ack.acknowledge()
            logger.info(
                "Stock reservation released: eventId=${event.id}, orderId=${orderCancelled.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderCancelledEvent: eventId=${event.id}, orderId=${orderCancelled.orderId}",
                e,
            )
            throw e
        }
    }
}
