package com.koosco.inventoryservice.application.contract

import com.koosco.common.core.event.CloudEvent

/**
 * fileName       : InventoryIntegrationEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:30
 * description    :
 */
interface InventoryIntegrationEvent {
    val orderId: Long

    /**
     * CloudEvent type
     * 예: stock.reserve.failed
     */
    fun getEventType(): String

    /**
     * Kafka partition key
     */
    fun getPartitionKey(): String = orderId.toString()

    /**
     * CloudEvent subject (선택)
     */
    fun getSubject(): String = "inventory/$orderId"

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
