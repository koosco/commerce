package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.enums.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("OrderStatusHistory 테스트")
class OrderStatusHistoryTest {

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        fun `상태 이력 생성 - fromStatus null (최초 생성)`() {
            val history = OrderStatusHistory.create(
                orderId = 1L,
                fromStatus = null,
                toStatus = OrderStatus.CREATED,
            )

            assertThat(history.orderId).isEqualTo(1L)
            assertThat(history.fromStatus).isNull()
            assertThat(history.toStatus).isEqualTo(OrderStatus.CREATED)
            assertThat(history.reason).isNull()
            assertThat(history.createdAt).isNotNull()
        }

        @Test
        fun `상태 이력 생성 - 사유 포함`() {
            val history = OrderStatusHistory.create(
                orderId = 1L,
                fromStatus = OrderStatus.PAYMENT_PENDING,
                toStatus = OrderStatus.CANCELLED,
                reason = "사용자 요청",
            )

            assertThat(history.fromStatus).isEqualTo(OrderStatus.PAYMENT_PENDING)
            assertThat(history.toStatus).isEqualTo(OrderStatus.CANCELLED)
            assertThat(history.reason).isEqualTo("사용자 요청")
        }
    }
}
