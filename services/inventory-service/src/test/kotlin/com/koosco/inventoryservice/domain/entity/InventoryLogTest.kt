package com.koosco.inventoryservice.domain.entity

import com.koosco.inventoryservice.domain.enums.InventoryAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("InventoryLog")
class InventoryLogTest {

    @Test
    fun `로그를 생성한다 - orderId 포함`() {
        val log = InventoryLog(
            skuId = "SKU-001",
            orderId = 100L,
            action = InventoryAction.RESERVE,
            quantity = 5,
        )

        assertThat(log.id).isEqualTo(0)
        assertThat(log.skuId).isEqualTo("SKU-001")
        assertThat(log.orderId).isEqualTo(100L)
        assertThat(log.action).isEqualTo(InventoryAction.RESERVE)
        assertThat(log.quantity).isEqualTo(5)
        assertThat(log.createdAt).isNotNull()
    }

    @Test
    fun `로그를 생성한다 - orderId null`() {
        val log = InventoryLog(
            skuId = "SKU-001",
            orderId = null,
            action = InventoryAction.ADD,
            quantity = 10,
        )

        assertThat(log.orderId).isNull()
        assertThat(log.action).isEqualTo(InventoryAction.ADD)
    }
}
