package com.koosco.orderservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.application.command.MarkRefundCompletedCommand
import com.koosco.orderservice.application.usecase.MarkRefundCompletedUseCase
import com.koosco.orderservice.contract.inbound.payment.PaymentCanceledEvent
import com.koosco.orderservice.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@Validated
class KafkaPaymentCanceledConsumer(
    private val markRefundCompletedUseCase: MarkRefundCompletedUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.canceled}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onPaymentCanceled(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("PaymentCanceledEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val paymentCanceled = try {
            objectMapper.convertValue(payload, PaymentCanceledEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize PaymentCanceledEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_REFUND_COMPLETED)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${paymentCanceled.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentCanceledEvent: eventId=${event.id}, orderId=${paymentCanceled.orderId}, " +
                "canceledAmount=${paymentCanceled.canceledAmount}",
        )

        val context = MessageContext(
            correlationId = paymentCanceled.orderId.toString(),
            causationId = event.id,
        )

        try {
            markRefundCompletedUseCase.execute(
                MarkRefundCompletedCommand(
                    orderId = paymentCanceled.orderId,
                    canceledAmount = paymentCanceled.canceledAmount,
                    isFullyCanceled = paymentCanceled.isFullyCanceled,
                ),
                context,
            )

            idempotencyChecker.recordProcessed(
                messageId = event.id,
                action = Actions.MARK_REFUND_COMPLETED,
                aggregateId = paymentCanceled.orderId.toString(),
            )

            ack.acknowledge()

            logger.info(
                "Successfully processed PaymentCanceled: eventId=${event.id}, orderId=${paymentCanceled.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process PaymentCanceledEvent: eventId=${event.id}, orderId=${paymentCanceled.orderId}",
                e,
            )
            throw e
        }
    }
}
