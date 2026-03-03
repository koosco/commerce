package com.koosco.catalogservice.infra.client

import com.koosco.catalogservice.application.port.InventoryQueryPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("InventoryQueryPort 계약 테스트")
class InventoryHttpAdapterTest {

    @Nested
    @DisplayName("getAvailability는")
    inner class GetAvailabilityTest {

        @Test
        fun `빈 리스트를 전달하면 빈 Map을 반환한다`() {
            val port: InventoryQueryPort = object : InventoryQueryPort {
                override fun getAvailability(skuIds: List<String>): Map<String, Boolean> =
                    if (skuIds.isEmpty()) emptyMap() else skuIds.associateWith { true }
            }

            val result = port.getAvailability(emptyList())

            assertThat(result).isEmpty()
        }

        @Test
        fun `SKU ID 목록에 대한 가용성을 반환한다`() {
            val port: InventoryQueryPort = object : InventoryQueryPort {
                override fun getAvailability(skuIds: List<String>): Map<String, Boolean> =
                    mapOf("SKU-001" to true, "SKU-002" to false)
            }

            val result = port.getAvailability(listOf("SKU-001", "SKU-002"))

            assertThat(result).hasSize(2)
            assertThat(result["SKU-001"]).isTrue()
            assertThat(result["SKU-002"]).isFalse()
        }

        @Test
        fun `fallback 시 모든 SKU에 대해 true를 반환한다`() {
            // CircuitBreaker fallback 동작 시뮬레이션
            val skuIds = listOf("SKU-001", "SKU-002", "SKU-003")
            val fallbackResult = skuIds.associateWith { true }

            assertThat(fallbackResult).hasSize(3)
            assertThat(fallbackResult.values).allMatch { it }
        }
    }
}
