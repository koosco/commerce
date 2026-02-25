package com.koosco.paymentservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.paymentservice.application.command.CancelPaymentByOrderCommand
import com.koosco.paymentservice.application.usecase.CancelPaymentByOrderUseCase
import com.koosco.paymentservice.contract.inbound.order.OrderRefundRequestedEvent
import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import com.koosco.paymentservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@Validated
class KafkaOrderRefundRequestedConsumer(
    private val cancelPaymentByOrderUseCase: CancelPaymentByOrderUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${payment.topic.mappings.order.refund.requested}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderRefundRequested(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val rawData = event.data
            ?: run {
                logger.error("OrderRefundRequestedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val refundEvent = try {
            objectMapper.convertValue(rawData, OrderRefundRequestedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderRefundRequestedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        logger.info(
            "Received OrderRefundRequestedEvent: eventId=${event.id}, orderId=${refundEvent.orderId}",
        )

        if (idempotencyChecker.isAlreadyProcessed(event.id, PaymentIdempotency.Companion.Actions.CANCEL)) {
            logger.info("Event already processed: eventId=${event.id}")
            ack.acknowledge()
            return
        }

        try {
            cancelPaymentByOrderUseCase.execute(
                CancelPaymentByOrderCommand(
                    orderId = refundEvent.orderId,
                    cancelAmount = refundEvent.refundAmount,
                ),
            )

            idempotencyChecker.recordProcessed(
                event.id,
                PaymentIdempotency.Companion.Actions.CANCEL,
                refundEvent.orderId.toString(),
            )

            ack.acknowledge()

            logger.info(
                "Successfully processed refund: eventId=${event.id}, orderId=${refundEvent.orderId}, " +
                    "refundAmount=${refundEvent.refundAmount}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderRefundRequestedEvent: eventId=${event.id}, orderId=${refundEvent.orderId}",
                e,
            )
            throw e
        }
    }
}
