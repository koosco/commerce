package com.koosco.catalogservice.product.application.contract

import com.koosco.common.core.event.CloudEvent

/**
 * fileName       : CatalogIntegrationEvent
 * author         : koo
 * date           : 2025. 12. 22. 오전 9:30
 * description    :
 */
interface ProductIntegrationEvent {
    val skuId: String

    /**
     * CloudEvent type
     * 예: stock.reserve.failed
     */
    fun getEventType(): String

    /**
     * Kafka partition key
     */
    fun getPartitionKey(): String = skuId

    /**
     * CloudEvent subject (선택)
     */
    fun getSubject(): String = "sku/$skuId"

    /**
     * CloudEvent 변환 (공통)
     */
    fun toCloudEvent(source: String): CloudEvent<ProductIntegrationEvent> = CloudEvent.of(
        source = source,
        type = getEventType(),
        subject = getSubject(),
        data = this,
    )
}
