package com.koosco.paymentservice.contract

import com.koosco.common.core.event.CloudEvent

/**
 * fileName       : PaymentIntegrationEvent
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:09
 * description    :
 */
interface PaymentIntegrationEvent {

    val paymentId: String

    /**
     * CloudEvent type
     */
    fun getEventType(): String

    /**
     * Kafka partition key
     */
    fun getPartitionKey(): String = paymentId

    /**
     * CloudEvent subject
     */
    fun getSubject(): String = "payment/$paymentId"

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
