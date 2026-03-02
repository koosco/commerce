package com.koosco.inventoryservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("InventoryEventIdempotency")
class InventoryEventIdempotencyTest {

    @Nested
    @DisplayName("create 팩토리 메서드")
    inner class Create {

        @Test
        fun `멱등성 엔트리를 생성한다`() {
            val entry = InventoryEventIdempotency.create(
                messageId = "msg-123",
                action = InventoryEventIdempotency.Companion.Actions.RESERVE_STOCK,
                aggregateId = "SKU-001",
            )

            assertThat(entry.messageId).isEqualTo("msg-123")
            assertThat(entry.action).isEqualTo("RESERVE_STOCK")
            assertThat(entry.aggregateId).isEqualTo("SKU-001")
            assertThat(entry.aggregateType).isEqualTo("Inventory")
            assertThat(entry.id).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("Actions 상수")
    inner class Actions {

        @Test
        fun `상수 값을 확인한다`() {
            assertThat(InventoryEventIdempotency.Companion.Actions.RESERVE_STOCK).isEqualTo("RESERVE_STOCK")
            assertThat(InventoryEventIdempotency.Companion.Actions.CONFIRM_STOCK).isEqualTo("CONFIRM_STOCK")
            assertThat(InventoryEventIdempotency.Companion.Actions.RELEASE_STOCK).isEqualTo("RELEASE_STOCK")
            assertThat(InventoryEventIdempotency.Companion.Actions.INITIALIZE_STOCK).isEqualTo("INITIALIZE_STOCK")
        }
    }
}
