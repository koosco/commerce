package com.koosco.orderservice.domain.entity

import com.koosco.common.core.outbox.OutboxStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("OrderOutboxEntry 테스트")
class OrderOutboxEntryTest {

    @Test
    fun `Outbox 엔트리 생성`() {
        val entry = OrderOutboxEntry.create(
            aggregateId = "order-1",
            eventType = "order.placed",
            payload = "{\"orderId\": 1}",
            topic = "order.placed",
            partitionKey = "1",
        )

        assertThat(entry.aggregateId).isEqualTo("order-1")
        assertThat(entry.aggregateType).isEqualTo("Order")
        assertThat(entry.eventType).isEqualTo("order.placed")
        assertThat(entry.payload).isEqualTo("{\"orderId\": 1}")
        assertThat(entry.status).isEqualTo(OutboxStatus.PENDING)
        assertThat(entry.topic).isEqualTo("order.placed")
        assertThat(entry.partitionKey).isEqualTo("1")
        assertThat(entry.createdAt).isNotNull()
    }
}
