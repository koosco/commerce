package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderConfirmedUseCase
import com.koosco.orderservice.order.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.order.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 재고 확정 완료 이벤트 핸들러
 */
@Component
@Validated
class KafkaStockConfirmedConsumer(
    private val markOrderConfirmedUseCase: MarkOrderConfirmedUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.confirmed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onStockConfirmed(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockConfirmedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockConfirmedEvent = try {
            objectMapper.convertValue(payload, StockConfirmedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockConfirmedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_CONFIRMED)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}")
            ack.acknowledge()
            return
        }

        val context = MessageContext(
            correlationId = stockConfirmedEvent.correlationId,
            causationId = event.id,
        )
        logger.info("Processing StockConfirmedEvent: orderId=${stockConfirmedEvent.orderId}, context=$context")

        try {
            markOrderConfirmedUseCase.execute(
                MarkOrderConfirmedCommand(
                    orderId = stockConfirmedEvent.orderId,
                    reservationId = stockConfirmedEvent.reservationId,
                    items = stockConfirmedEvent.items.map {
                        MarkOrderConfirmedCommand.MarkedConfirmedItem(it.skuId, it.quantity)
                    },
                ),
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.MARK_CONFIRMED,
                orderId = stockConfirmedEvent.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully confirmed order: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process StockConfirmed event: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}",
                e,
            )
            throw e
        }
    }
}
