package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderFailedCommand
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReserveFailedEvent
import com.koosco.orderservice.order.application.usecase.CancelOrderByStockFailureUseCase
import com.koosco.orderservice.order.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.order.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 재고 예약 실패 이벤트 핸들러
 */
@Component
@Validated
class KafkaStockReservationFailedConsumer(
    private val cancelOrderByStockFailureUseCase: CancelOrderByStockFailureUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.reservation.failed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onStockReservationFailed(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockReserveFailedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockReserveFailed = try {
            objectMapper.convertValue(payload, StockReserveFailedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockReserveFailedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_FAILED_BY_STOCK_RESERVATION)) {
            logger.info(
                "Event already processed: eventId=${event.id}, orderId=${stockReserveFailed.orderId}",
            )
            ack.acknowledge()
            return
        }

        logger.info(
            "Received StockReserveFailedEvent: eventId=${event.id}, orderId=${stockReserveFailed.orderId}, reason=${stockReserveFailed.reason}",
        )

        val context = MessageContext(
            correlationId = stockReserveFailed.correlationId,
            causationId = event.id,
        )

        try {
            cancelOrderByStockFailureUseCase.execute(
                MarkOrderFailedCommand(
                    orderId = stockReserveFailed.orderId,
                    reason = stockReserveFailed.reason,
                ),
                context,
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.MARK_FAILED_BY_STOCK_RESERVATION,
                orderId = stockReserveFailed.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully marked order as failed: eventId=${event.id}, orderId=${stockReserveFailed.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process StockReserveFailed event: eventId=${event.id}, orderId=${stockReserveFailed.orderId}",
                e,
            )
            throw e
        }
    }
}
