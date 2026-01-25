package com.koosco.paymentservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.contract.inbound.order.OrderPlacedEvent
import com.koosco.paymentservice.application.usecase.CreatePaymentUseCase
import com.koosco.paymentservice.domain.exception.DuplicatePaymentException
import com.koosco.paymentservice.domain.exception.PaymentBusinessException
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * fileName       : KafkaOrderCreateEventConsumer
 * author         : koo
 * date           : 2025. 12. 22. 오전 3:55
 * description    :
 */
@Component
@Validated
class KafkaOrderPlacedEventConsumer(private val createPaymentUseCase: CreatePaymentUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${payment.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val rawData = event.data
            ?: run {
                logger.error("OrderPlacedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderPlacedEvent = try {
            objectMapper.convertValue(rawData, OrderPlacedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderPlacedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        logger.info(
            "Received OrderPlacedEvent: eventId=${event.id}, " +
                "orderId=${orderPlacedEvent.orderId}",
        )

        val command = CreatePaymentCommand(
            orderId = orderPlacedEvent.orderId,
            userId = orderPlacedEvent.userId,
            amount = orderPlacedEvent.payableAmount,
        )

        val context = MessageContext(
            correlationId = orderPlacedEvent.correlationId,
            causationId = event.id,
        )

        try {
            createPaymentUseCase.execute(command, context)
            ack.acknowledge()
        } catch (e: DuplicatePaymentException) {
            logger.info("Payment already exists: orderId=${command.orderId}")
            ack.acknowledge()
        } catch (e: PaymentBusinessException) {
            // 비즈니스 실패 → 보상 이벤트 발행 가능
            ack.acknowledge()
        } catch (e: Exception) {
            logger.error("Transient failure, will retry", e)
            throw e // ❗ ack 안 함 → 재시도
        }
    }
}
