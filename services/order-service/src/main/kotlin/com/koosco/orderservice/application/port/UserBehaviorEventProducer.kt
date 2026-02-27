package com.koosco.orderservice.application.port

import com.koosco.common.core.event.UserBehaviorEvent

/**
 * 사용자 행동 이벤트 발행 포트.
 * 분석 목적의 이벤트로 outbox 패턴 대신 직접 Kafka 발행.
 */
interface UserBehaviorEventProducer {
    fun publish(event: UserBehaviorEvent)
}
