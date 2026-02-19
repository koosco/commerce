package com.koosco.orderservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.application.usecase.CancelOrderByStockConfirmFailureUseCase
import com.koosco.orderservice.contract.inbound.inventory.StockConfirmFailedEvent
import com.koosco.orderservice.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 재고 확정 실패 이벤트 핸들러
 *
 * inventory-service에서 재고 확정이 실패했을 때 호출됩니다.
 * 주문을 취소하고 보상 트랜잭션(재고 예약 해제, 환불)을 트리거합니다.
 */
@Component
@Validated
class KafkaStockConfirmFailedConsumer(
    private val cancelOrderByStockConfirmFailureUseCase: CancelOrderByStockConfirmFailureUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.confirm.failed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onStockConfirmFailed(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockConfirmFailedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockConfirmFailed = try {
            objectMapper.convertValue(payload, StockConfirmFailedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockConfirmFailedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.CANCEL_BY_STOCK_CONFIRM_FAILURE)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${stockConfirmFailed.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received StockConfirmFailedEvent: eventId=${event.id}, orderId=${stockConfirmFailed.orderId}, reason=${stockConfirmFailed.reason}",
        )

        val context = MessageContext(
            correlationId = stockConfirmFailed.correlationId,
            causationId = event.id,
        )

        try {
            cancelOrderByStockConfirmFailureUseCase.execute(
                orderId = stockConfirmFailed.orderId,
                context = context,
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.CANCEL_BY_STOCK_CONFIRM_FAILURE,
                orderId = stockConfirmFailed.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully cancelled order by stock confirm failure: eventId=${event.id}, orderId=${stockConfirmFailed.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process StockConfirmFailed event: eventId=${event.id}, orderId=${stockConfirmFailed.orderId}",
                e,
            )
            throw e
        }
    }
}
