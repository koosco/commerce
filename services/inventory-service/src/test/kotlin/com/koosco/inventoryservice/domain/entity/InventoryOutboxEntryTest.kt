package com.koosco.inventoryservice.domain.entity

import com.koosco.common.core.outbox.OutboxStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("InventoryOutboxEntry")
class InventoryOutboxEntryTest {

    @Test
    fun `팩토리 메서드로 OutboxEntry를 생성한다`() {
        val entry = InventoryOutboxEntry.create(
            aggregateId = "SKU-001",
            eventType = "stock.reserved",
            payload = """{"orderId":1}""",
            topic = "koosco.commerce.stock.reserved",
            partitionKey = "1",
        )

        assertThat(entry.aggregateId).isEqualTo("SKU-001")
        assertThat(entry.aggregateType).isEqualTo("Inventory")
        assertThat(entry.eventType).isEqualTo("stock.reserved")
        assertThat(entry.payload).isEqualTo("""{"orderId":1}""")
        assertThat(entry.topic).isEqualTo("koosco.commerce.stock.reserved")
        assertThat(entry.partitionKey).isEqualTo("1")
        assertThat(entry.status).isEqualTo(OutboxStatus.PENDING)
        assertThat(entry.id).isEqualTo(0)
    }
}
