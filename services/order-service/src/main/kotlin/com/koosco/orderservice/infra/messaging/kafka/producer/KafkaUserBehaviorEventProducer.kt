package com.koosco.orderservice.infra.messaging.kafka.producer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.event.UserBehaviorEvent
import com.koosco.orderservice.application.port.UserBehaviorEventProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Kafka 기반 사용자 행동 이벤트 발행.
 * 분석 목적의 이벤트이므로 outbox 패턴 대신 직접 Kafka로 발행한다.
 * userId 기반 파티셔닝을 통해 동일 사용자의 이벤트가 같은 파티션으로 전달된다.
 */
@Component
class KafkaUserBehaviorEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,

    @Value("\${order.topic.user-behavior:user-behavior-events}")
    private val topic: String,

    @Value("\${spring.application.name}")
    private val source: String,
) : UserBehaviorEventProducer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: UserBehaviorEvent) {
        val cloudEvent = CloudEvent.of(
            source = source,
            type = "user.behavior.${event.behaviorType.name.lowercase()}",
            subject = "user/${event.userId}",
            data = event,
        )

        try {
            kafkaTemplate.send(topic, event.userId.toString(), cloudEvent)
            logger.debug(
                "Published user behavior event: type={}, userId={}, productId={}",
                event.behaviorType,
                event.userId,
                event.productId,
            )
        } catch (e: Exception) {
            logger.warn(
                "Failed to publish user behavior event: type={}, userId={}",
                event.behaviorType,
                event.userId,
                e,
            )
        }
    }
}
