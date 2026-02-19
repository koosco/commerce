package com.koosco.orderservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.application.contract.inbound.payment.PaymentCompletedEvent
import com.koosco.orderservice.application.usecase.MarkOrderPaidUseCase
import com.koosco.orderservice.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 결제 완료 이벤트 핸들러
 */
@Component
@Validated
class KafkaPaymentCompletedConsumer(
    private val markOrderPaidUseCase: MarkOrderPaidUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.completed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onPaymentCompleted(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("PaymentCompleted is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val paymentCompleted = try {
            objectMapper.convertValue(payload, PaymentCompletedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize PaymentCompletedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_PAID)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${paymentCompleted.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentCompleted: eventId=${event.id}, orderId=${paymentCompleted.orderId}",
        )

        val context = MessageContext(
            correlationId = paymentCompleted.correlationId,
            causationId = event.id,
        )

        try {
            markOrderPaidUseCase.execute(
                MarkOrderPaidCommand(
                    orderId = paymentCompleted.orderId,
                    paidAmount = paymentCompleted.paidAmount,
                ),
                context,
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.MARK_PAID,
                orderId = paymentCompleted.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully marked order as paid: eventId=${event.id}, orderId=${paymentCompleted.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process PaymentCompleted event: eventId=${event.id}, orderId=${paymentCompleted.orderId}",
                e,
            )
            // Don't ack - will retry
            throw e
        }
    }
}
