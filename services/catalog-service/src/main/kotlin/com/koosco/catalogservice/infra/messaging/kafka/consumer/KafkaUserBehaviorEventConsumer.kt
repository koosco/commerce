package com.koosco.catalogservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.event.UserBehaviorEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 사용자 행동 이벤트를 소비하여 상품별 조회수를 Redis에 집계한다.
 * VIEW 이벤트만 처리하며 productId가 있는 경우에만 카운트를 증가시킨다.
 */
@Component
@Validated
class KafkaUserBehaviorEventConsumer(private val redisTemplate: StringRedisTemplate) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${catalog.topic.consumer.user-behavior}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onUserBehaviorEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("UserBehaviorEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        try {
            when (event.type) {
                EVENT_TYPE_VIEW -> handleViewEvent(event, payload)
                else -> logger.debug("Ignoring unhandled behavior event type: ${event.type}")
            }
        } catch (e: Exception) {
            logger.error(
                "Failed to process user behavior event: eventId=${event.id}, type=${event.type}",
                e,
            )
            throw e
        }

        ack.acknowledge()
    }

    private fun handleViewEvent(event: CloudEvent<*>, payload: Any) {
        val behaviorEvent = try {
            objectMapper.convertValue(payload, UserBehaviorEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize UserBehaviorEvent: eventId=${event.id}", e)
            return
        }

        val productId = behaviorEvent.productId ?: return

        redisTemplate.opsForValue().increment(viewCountKey(productId))

        logger.debug(
            "Incremented view count: productId={}, userId={}",
            productId,
            behaviorEvent.userId,
        )
    }

    companion object {
        private const val EVENT_TYPE_VIEW = "user.behavior.view"
        private const val VIEW_COUNT_PREFIX = "catalog:product:viewCount:"

        fun viewCountKey(productId: Long): String = "$VIEW_COUNT_PREFIX$productId"
    }
}
