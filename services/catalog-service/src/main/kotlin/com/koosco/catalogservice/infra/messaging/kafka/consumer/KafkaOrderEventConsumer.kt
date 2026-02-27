package com.koosco.catalogservice.infra.messaging.kafka.consumer

import com.koosco.catalogservice.application.usecase.UpdateProductSalesCountUseCase
import com.koosco.catalogservice.contract.inbound.order.OrderCancelledEvent
import com.koosco.catalogservice.contract.inbound.order.OrderConfirmedEvent
import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * order-service에서 발행하는 주문 확정/취소 이벤트를 소비하여
 * 상품의 salesCount를 증가/감소시킨다.
 */
@Component
@Validated
class KafkaOrderEventConsumer(private val updateProductSalesCountUseCase: UpdateProductSalesCountUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${catalog.topic.consumer.order.confirmed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderConfirmedEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderConfirmedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        try {
            handleOrderConfirmed(event, payload)
        } catch (e: Exception) {
            logger.error(
                "Failed to process order confirmed event: eventId=${event.id}",
                e,
            )
            throw e
        }

        ack.acknowledge()
    }

    @KafkaListener(
        topics = ["\${catalog.topic.consumer.order.cancelled}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderCancelledEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderCancelledEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        try {
            handleOrderCancelled(event, payload)
        } catch (e: Exception) {
            logger.error(
                "Failed to process order cancelled event: eventId=${event.id}",
                e,
            )
            throw e
        }

        ack.acknowledge()
    }

    private fun handleOrderConfirmed(event: CloudEvent<*>, payload: Any) {
        val orderConfirmed = try {
            objectMapper.convertValue(payload, OrderConfirmedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderConfirmedEvent: eventId=${event.id}", e)
            return
        }

        logger.info(
            "Received OrderConfirmedEvent: eventId={}, orderId={}, itemCount={}",
            event.id,
            orderConfirmed.orderId,
            orderConfirmed.items.size,
        )

        orderConfirmed.items.forEach { item ->
            updateProductSalesCountUseCase.incrementSalesCount(
                orderId = orderConfirmed.orderId,
                skuId = item.skuId,
                quantity = item.quantity,
            )
        }
    }

    private fun handleOrderCancelled(event: CloudEvent<*>, payload: Any) {
        val orderCancelled = try {
            objectMapper.convertValue(payload, OrderCancelledEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderCancelledEvent: eventId=${event.id}", e)
            return
        }

        logger.info(
            "Received OrderCancelledEvent: eventId={}, orderId={}, itemCount={}",
            event.id,
            orderCancelled.orderId,
            orderCancelled.items.size,
        )

        orderCancelled.items.forEach { item ->
            updateProductSalesCountUseCase.decrementSalesCount(
                orderId = orderCancelled.orderId,
                skuId = item.skuId,
                quantity = item.quantity,
            )
        }
    }
}
