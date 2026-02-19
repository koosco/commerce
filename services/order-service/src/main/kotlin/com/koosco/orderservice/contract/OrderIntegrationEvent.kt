package com.koosco.orderservice.contract

import com.koosco.common.core.event.CloudEvent

/**
 * fileName       : OrderIntegrationEvents
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:46
 * description    :
 */
interface OrderIntegrationEvent {

    val orderId: Long

    /**
     * CloudEvent type
     */
    fun getEventType(): String

    /**
     * Kafka partition key
     */
    fun getPartitionKey(): String = orderId.toString()

    /**
     * CloudEvent subject
     */
    fun getSubject(): String = "order/$orderId"

    /**
     * CloudEvent 변환 (공통)
     */
    fun toCloudEvent(source: String): CloudEvent<Any> = CloudEvent.of(
        source = source,
        type = getEventType(),
        subject = getSubject(),
        data = this,
    )
}
