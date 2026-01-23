package com.koosco.catalogservice.product.application.port

import com.koosco.common.core.event.DomainEvent

/**
 * 도메인 이벤트 발행 인터페이스
 * - UoW 패턴과 함께 사용하여 트랜잭션 내에서 발생한 도메인 이벤트를 발행
 */
interface DomainEventPublisher {
    fun publish(event: DomainEvent)

    fun publishAll(events: List<DomainEvent>)
}
