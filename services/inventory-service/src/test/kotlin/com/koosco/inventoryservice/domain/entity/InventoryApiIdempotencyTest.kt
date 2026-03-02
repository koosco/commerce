package com.koosco.inventoryservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("InventoryApiIdempotency")
class InventoryApiIdempotencyTest {

    @Nested
    @DisplayName("create 팩토리 메서드")
    inner class Create {

        @Test
        fun `API 멱등성 엔트리를 생성한다`() {
            val entry = InventoryApiIdempotency.create(
                idempotencyKey = "key-123",
                operationType = InventoryApiIdempotency.ADD_STOCK,
            )

            assertThat(entry.idempotencyKey).isEqualTo("key-123")
            assertThat(entry.operationType).isEqualTo("ADD_STOCK")
            assertThat(entry.id).isNull()
        }
    }

    @Nested
    @DisplayName("상수")
    inner class Constants {

        @Test
        fun `상수 값을 확인한다`() {
            assertThat(InventoryApiIdempotency.ADD_STOCK).isEqualTo("ADD_STOCK")
            assertThat(InventoryApiIdempotency.DECREASE_STOCK).isEqualTo("DECREASE_STOCK")
        }
    }
}
