package com.koosco.searchservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.searchservice.application.usecase.sync.SearchProductSyncUseCase
import com.koosco.searchservice.contract.inbound.catalog.ProductChangedEvent
import com.koosco.searchservice.contract.inbound.catalog.ProductDeletedEvent
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * catalog-service에서 발행하는 상품 변경/삭제 이벤트를 소비하여
 * 비정규화된 search_product 테이블을 동기화한다.
 */
@Component
@Validated
class KafkaProductEventConsumer(private val searchProductSyncUseCase: SearchProductSyncUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${search.topic.consumer.product}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onProductEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("Product event data is null: eventId={}", event.id)
                ack.acknowledge()
                return
            }

        try {
            when (event.type) {
                EVENT_TYPE_PRODUCT_CHANGED -> handleProductChanged(event, payload)
                EVENT_TYPE_PRODUCT_DELETED -> handleProductDeleted(event, payload)
                else -> logger.debug("Ignoring unhandled event type: type={}, eventId={}", event.type, event.id)
            }
        } catch (e: Exception) {
            logger.error("Failed to process product event: eventId={}, type={}", event.id, event.type, e)
            throw e
        }

        ack.acknowledge()
    }

    private fun handleProductChanged(event: CloudEvent<*>, payload: Any) {
        val productChanged = try {
            objectMapper.convertValue(payload, ProductChangedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize ProductChangedEvent: eventId={}", event.id, e)
            return
        }

        logger.info(
            "Received ProductChangedEvent: eventId={}, productId={}",
            event.id,
            productChanged.productId,
        )

        searchProductSyncUseCase.upsert(productChanged)
    }

    private fun handleProductDeleted(event: CloudEvent<*>, payload: Any) {
        val productDeleted = try {
            objectMapper.convertValue(payload, ProductDeletedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize ProductDeletedEvent: eventId={}", event.id, e)
            return
        }

        logger.info(
            "Received ProductDeletedEvent: eventId={}, productId={}",
            event.id,
            productDeleted.productId,
        )

        searchProductSyncUseCase.delete(productDeleted)
    }

    companion object {
        private const val EVENT_TYPE_PRODUCT_CHANGED = "product.status.changed"
        private const val EVENT_TYPE_PRODUCT_DELETED = "product.deleted"
    }
}
