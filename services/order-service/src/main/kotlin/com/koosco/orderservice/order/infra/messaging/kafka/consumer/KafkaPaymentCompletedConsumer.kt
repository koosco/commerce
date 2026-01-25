package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.order.application.contract.inbound.payment.PaymentCompletedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderPaidUseCase
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
class KafkaPaymentCompletedConsumer(private val markOrderPaidUseCase: MarkOrderPaidUseCase) {

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

        logger.info(
            "Received PaymentCompleted: eventId=${event.id}, " +
                "orderId=${paymentCompleted.orderId}, paymentId=...",
        )

        val context = MessageContext(
            correlationId = paymentCompleted.correlationId,
            causationId = event.id,
        )

        try {
            // 주문 확정
            markOrderPaidUseCase.execute(
                MarkOrderPaidCommand(
                    orderId = paymentCompleted.orderId,
                    paidAmount = paymentCompleted.paidAmount,
                ),
                context,
            )

            ack.acknowledge()

            logger.info(
                "Successfully confirm order for Order: eventId=${event.id}, orderId=${paymentCompleted.orderId}...",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process Payment Completed event: ${event.id}, orderId=${paymentCompleted.orderId}",
                e,
            )
        }
        // 일단 바로 환불 진행하도록 진행
        // TODO : 실패시 재처리 시도 후 환불 수행
    }
}
