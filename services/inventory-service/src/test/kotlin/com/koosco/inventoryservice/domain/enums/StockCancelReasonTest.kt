package com.koosco.inventoryservice.domain.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("StockCancelReason")
class StockCancelReasonTest {

    @Nested
    @DisplayName("mapCancelReason")
    inner class MapCancelReason {

        @Test
        fun `PAYMENT_FAILED 문자열을 매핑한다`() {
            assertThat(StockCancelReason.mapCancelReason("PAYMENT_FAILED"))
                .isEqualTo(StockCancelReason.PAYMENT_FAILED)
        }

        @Test
        fun `USER_CANCELLED 문자열을 매핑한다`() {
            assertThat(StockCancelReason.mapCancelReason("USER_CANCELLED"))
                .isEqualTo(StockCancelReason.USER_CANCELLED)
        }

        @Test
        fun `PAYMENT_TIMEOUT 문자열을 매핑한다`() {
            assertThat(StockCancelReason.mapCancelReason("PAYMENT_TIMEOUT"))
                .isEqualTo(StockCancelReason.PAYMENT_TIMEOUT)
        }

        @Test
        fun `소문자도 매핑된다`() {
            assertThat(StockCancelReason.mapCancelReason("payment_failed"))
                .isEqualTo(StockCancelReason.PAYMENT_FAILED)
        }

        @ParameterizedTest
        @ValueSource(strings = ["UNKNOWN", "INVALID", ""])
        fun `알 수 없는 값은 USER_CANCELLED로 매핑된다`(reason: String) {
            assertThat(StockCancelReason.mapCancelReason(reason))
                .isEqualTo(StockCancelReason.USER_CANCELLED)
        }

        @Test
        fun `null이면 USER_CANCELLED로 매핑된다`() {
            assertThat(StockCancelReason.mapCancelReason(null))
                .isEqualTo(StockCancelReason.USER_CANCELLED)
        }
    }
}
