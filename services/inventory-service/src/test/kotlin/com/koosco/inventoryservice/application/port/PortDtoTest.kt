package com.koosco.inventoryservice.application.port

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Port DTOs")
class PortDtoTest {

    @Test
    fun `SnapshotStockView를 생성한다`() {
        val view = InventoryStockSnapshotQueryPort.SnapshotStockView(
            skuId = "SKU-001",
            total = 100,
            reserved = 30,
        )

        assertThat(view.skuId).isEqualTo("SKU-001")
        assertThat(view.total).isEqualTo(100)
        assertThat(view.reserved).isEqualTo(30)
    }
}
