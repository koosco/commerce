package com.koosco.orderservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.application.command.MarkOrderPaymentPendingCommand
import com.koosco.orderservice.application.usecase.MarkOrderPaymentPendingUseCase
import com.koosco.orderservice.contract.inbound.inventory.StockReservedEvent
import com.koosco.orderservice.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 재고 예약 완료 이벤트 핸들러
 */
@Component
@Validated
class KafkaStockReservedConsumer(
    private val markOrderPaymentPendingUseCase: MarkOrderPaymentPendingUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.reserved}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onStockReserved(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockReservedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockReserved = try {
            objectMapper.convertValue(payload, StockReservedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockReservedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_PAYMENT_PENDING)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${stockReserved.orderId}")
            ack.acknowledge()
            return
        }

        val context = MessageContext(
            correlationId = stockReserved.correlationId,
            causationId = event.id,
        )
        logger.info("Processing StockReservedEvent: orderId=${stockReserved.orderId}, context=$context")

        try {
            markOrderPaymentPendingUseCase.execute(
                MarkOrderPaymentPendingCommand(
                    orderId = stockReserved.orderId,
                ),
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                messageId = event.id,
                action = Actions.MARK_PAYMENT_PENDING,
                aggregateId = stockReserved.orderId.toString(),
            )

            ack.acknowledge()

            logger.info(
                "Successfully marked payment pending: eventId=${event.id}, orderId=${stockReserved.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process StockReserved event: eventId=${event.id}, orderId=${stockReserved.orderId}",
                e,
            )
            throw e
        }
    }
}
