package com.koosco.catalogservice.domain.entity

import com.koosco.common.core.outbox.OutboxStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CatalogOutboxEntry 테스트")
class CatalogOutboxEntryTest {

    @Nested
    @DisplayName("create는")
    inner class CreateTest {

        @Test
        fun `아웃박스 엔트리를 생성한다`() {
            val entry = CatalogOutboxEntry.create(
                aggregateId = "123",
                eventType = "ProductSkuCreated",
                payload = """{"skuId":"SKU-001"}""",
                topic = "koosco.commerce.product.default",
                partitionKey = "123",
            )

            assertThat(entry.aggregateId).isEqualTo("123")
            assertThat(entry.eventType).isEqualTo("ProductSkuCreated")
            assertThat(entry.payload).contains("SKU-001")
            assertThat(entry.topic).isEqualTo("koosco.commerce.product.default")
            assertThat(entry.partitionKey).isEqualTo("123")
            assertThat(entry.status).isEqualTo(OutboxStatus.PENDING)
            assertThat(entry.aggregateType).isEqualTo("Catalog")
            assertThat(entry.createdAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("상태 변경은")
    inner class StatusTest {

        @Test
        fun `status를 변경할 수 있다`() {
            val entry = CatalogOutboxEntry.create(
                aggregateId = "1",
                eventType = "Test",
                payload = "{}",
                topic = "topic",
                partitionKey = "1",
            )

            entry.status = OutboxStatus.PUBLISHED

            assertThat(entry.status).isEqualTo(OutboxStatus.PUBLISHED)
        }
    }
}
