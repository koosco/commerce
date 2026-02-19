package com.koosco.orderservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.application.command.CancelOrderCommand
import com.koosco.orderservice.application.contract.inbound.payment.PaymentFailedEvent
import com.koosco.orderservice.application.usecase.CancelOrderByPaymentFailureUseCase
import com.koosco.orderservice.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 결제 실패 이벤트 핸들러
 */
@Component
@Validated
class KafkaPaymentFailedConsumer(
    private val cancelOrderByPaymentFailureUseCase: CancelOrderByPaymentFailureUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.failed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onPaymentFailed(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("PaymentFailedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val paymentFailed = try {
            objectMapper.convertValue(payload, PaymentFailedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize PaymentFailedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.CANCEL_BY_PAYMENT_FAILURE)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${paymentFailed.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentFailedEvent: eventId=${event.id}, orderId=${paymentFailed.orderId}",
        )

        val context = MessageContext(
            correlationId = paymentFailed.correlationId,
            causationId = event.id,
        )

        try {
            cancelOrderByPaymentFailureUseCase.execute(
                CancelOrderCommand(
                    orderId = paymentFailed.orderId,
                    reason = OrderCancelReason.valueOf(paymentFailed.reason),
                ),
                context,
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.CANCEL_BY_PAYMENT_FAILURE,
                orderId = paymentFailed.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully cancelled order: eventId=${event.id}, orderId=${paymentFailed.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process PaymentFailed event: eventId=${event.id}, orderId=${paymentFailed.orderId}",
                e,
            )
            throw e
        }
    }
}
