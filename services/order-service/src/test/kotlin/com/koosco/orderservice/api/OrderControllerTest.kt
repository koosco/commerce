package com.koosco.orderservice.api

import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.api.controller.OrderController
import com.koosco.orderservice.api.request.CreateOrderRequest
import com.koosco.orderservice.api.request.OrderItemRequest
import com.koosco.orderservice.api.request.RefundOrderItemsRequest
import com.koosco.orderservice.api.request.ShippingAddressRequest
import com.koosco.orderservice.application.command.CancelOrderCommand
import com.koosco.orderservice.application.result.CreateOrderResult
import com.koosco.orderservice.application.result.OrderDetailResult
import com.koosco.orderservice.application.result.OrderItemDetailResult
import com.koosco.orderservice.application.result.OrderListResult
import com.koosco.orderservice.application.result.RefundOrderItemsResult
import com.koosco.orderservice.application.usecase.CancelOrderByUserUseCase
import com.koosco.orderservice.application.usecase.CreateOrderUseCase
import com.koosco.orderservice.application.usecase.GetOrderDetailUseCase
import com.koosco.orderservice.application.usecase.GetOrdersUseCase
import com.koosco.orderservice.application.usecase.RefundOrderItemsUseCase
import com.koosco.orderservice.domain.enums.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("OrderController 테스트")
class OrderControllerTest {

    private val createOrderUseCase: CreateOrderUseCase = mock()
    private val getOrdersUseCase: GetOrdersUseCase = mock()
    private val getOrderDetailUseCase: GetOrderDetailUseCase = mock()
    private val cancelOrderByUserUseCase: CancelOrderByUserUseCase = mock()
    private val refundOrderItemsUseCase: RefundOrderItemsUseCase = mock()

    private val controller = OrderController(
        createOrderUseCase,
        getOrdersUseCase,
        getOrderDetailUseCase,
        cancelOrderByUserUseCase,
        refundOrderItemsUseCase,
    )

    private val now = LocalDateTime.now()

    @Nested
    @DisplayName("POST /api/orders - 주문 생성")
    inner class CreateOrder {

        @Test
        fun `주문 생성 성공`() {
            val request = CreateOrderRequest(
                idempotencyKey = "key-1",
                items = listOf(
                    OrderItemRequest(
                        skuId = 1L,
                        productId = 1L,
                        brandId = 1L,
                        titleSnapshot = "상품1",
                        quantity = 2,
                        unitPrice = 10000L,
                    ),
                ),
                discountAmount = 0L,
                shippingFee = 0L,
                shippingAddress = ShippingAddressRequest(
                    "홍길동",
                    "010-1234-5678",
                    "12345",
                    "서울시",
                    "101호",
                ),
            )

            val result = CreateOrderResult(1L, "ORD-TEST", OrderStatus.CREATED, 20000L)
            whenever(createOrderUseCase.execute(any())).thenReturn(result)

            val response = controller.createOrder(1L, request)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.orderId).isEqualTo(1L)
            assertThat(response.data!!.orderNo).isEqualTo("ORD-TEST")
            assertThat(response.data!!.status).isEqualTo(OrderStatus.CREATED)
            assertThat(response.data!!.totalAmount).isEqualTo(20000L)
        }
    }

    @Nested
    @DisplayName("GET /api/orders - 주문 목록 조회")
    inner class GetOrders {

        @Test
        fun `주문 목록 조회 성공`() {
            val pageable = PageRequest.of(0, 20)
            val orderList = listOf(
                OrderListResult(1L, "ORD-1", 1L, OrderStatus.CREATED, 20000, 0, 0, 20000, "KRW", now, now),
            )
            whenever(getOrdersUseCase.execute(eq(1L), any())).thenReturn(PageImpl(orderList, pageable, 1))

            val response = controller.getOrders(1L, pageable)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.content).hasSize(1)
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId} - 주문 상세 조회")
    inner class GetOrderDetail {

        @Test
        fun `주문 상세 조회 성공`() {
            val detail = OrderDetailResult(
                orderId = 1L,
                orderNo = "ORD-1",
                userId = 1L,
                status = OrderStatus.CREATED,
                subtotalAmount = 20000,
                discountAmount = 0,
                shippingFee = 0,
                totalAmount = 20000,
                currency = "KRW",
                shippingAddressSnapshot = "{}",
                pricingSnapshot = null,
                items = listOf(
                    OrderItemDetailResult(1L, 1L, 1L, 1L, "상품1", null, 2, 10000, 20000),
                ),
                createdAt = now,
                updatedAt = now,
                placedAt = now,
                paidAt = null,
                canceledAt = null,
            )
            whenever(getOrderDetailUseCase.execute(1L, 1L)).thenReturn(detail)

            val response = controller.getOrderDetail(1L, 1L)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.orderId).isEqualTo(1L)
            assertThat(response.data!!.items).hasSize(1)
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{orderId}/cancel - 주문 취소")
    inner class CancelOrder {

        @Test
        fun `주문 취소 성공`() {
            val response = controller.cancelOrder(1L, 1L)

            assertThat(response.success).isTrue()
            verify(cancelOrderByUserUseCase).execute(any<CancelOrderCommand>(), any<MessageContext>())
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{orderId}/refund - 환불 요청")
    inner class RefundOrderItems {

        @Test
        fun `환불 요청 성공`() {
            val request = RefundOrderItemsRequest(itemIds = listOf(1L, 2L))
            val result = RefundOrderItemsResult(
                orderId = 1L,
                refundAmount = 30000L,
                refundedItemIds = listOf(1L, 2L),
                orderStatus = OrderStatus.REFUNDED,
            )
            whenever(refundOrderItemsUseCase.execute(any())).thenReturn(result)

            val response = controller.refundOrderItems(1L, 1L, request)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.orderId).isEqualTo(1L)
            assertThat(response.data!!.refundAmount).isEqualTo(30000L)
            assertThat(response.data!!.refundedItemIds).containsExactly(1L, 2L)
            assertThat(response.data!!.orderStatus).isEqualTo(OrderStatus.REFUNDED)
        }
    }
}
