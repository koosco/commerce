package com.koosco.inventoryservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("InventorySnapshot")
class InventorySnapshotTest {

    @Test
    fun `스냅샷을 생성한다`() {
        val now = LocalDateTime.of(2025, 1, 1, 0, 0)
        val snapshot = InventorySnapshot(
            skuId = "SKU-001",
            total = 100,
            reserved = 30,
            snapshottedAt = now,
        )

        assertThat(snapshot.skuId).isEqualTo("SKU-001")
        assertThat(snapshot.total).isEqualTo(100)
        assertThat(snapshot.reserved).isEqualTo(30)
        assertThat(snapshot.snapshottedAt).isEqualTo(now)
    }

    @Test
    fun `스냅샷 값을 변경할 수 있다`() {
        val now = LocalDateTime.of(2025, 1, 1, 0, 0)
        val snapshot = InventorySnapshot(
            skuId = "SKU-001",
            total = 100,
            reserved = 30,
            snapshottedAt = now,
        )

        snapshot.total = 200
        snapshot.reserved = 50
        snapshot.snapshottedAt = now.plusHours(1)

        assertThat(snapshot.total).isEqualTo(200)
        assertThat(snapshot.reserved).isEqualTo(50)
        assertThat(snapshot.snapshottedAt).isEqualTo(now.plusHours(1))
    }
}
