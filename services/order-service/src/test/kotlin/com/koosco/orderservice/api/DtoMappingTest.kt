package com.koosco.orderservice.api

import com.koosco.orderservice.api.request.AddCartItemRequest
import com.koosco.orderservice.api.request.CreateOrderRequest
import com.koosco.orderservice.api.request.OrderItemRequest
import com.koosco.orderservice.api.request.RefundOrderItemsRequest
import com.koosco.orderservice.api.request.ShippingAddressRequest
import com.koosco.orderservice.api.request.UpdateCartItemRequest
import com.koosco.orderservice.api.response.CartItemResponse
import com.koosco.orderservice.api.response.CartResponse
import com.koosco.orderservice.api.response.CreateOrderResponse
import com.koosco.orderservice.api.response.OrderDetailResponse
import com.koosco.orderservice.api.response.OrderItemResponse
import com.koosco.orderservice.api.response.OrderResponse
import com.koosco.orderservice.api.response.RefundOrderItemsResponse
import com.koosco.orderservice.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.application.result.CartResult
import com.koosco.orderservice.application.result.CreateOrderResult
import com.koosco.orderservice.application.result.OrderDetailResult
import com.koosco.orderservice.application.result.OrderItemDetailResult
import com.koosco.orderservice.application.result.OrderListResult
import com.koosco.orderservice.application.result.RefundOrderItemsResult
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("API DTO 매핑 테스트")
class DtoMappingTest {

    private val now = LocalDateTime.now()

    @Nested
    @DisplayName("CreateOrderRequest.toCommand")
    inner class CreateOrderRequestTest {

        @Test
        fun `CreateOrderRequest를 CreateOrderCommand로 변환`() {
            val request = CreateOrderRequest(
                idempotencyKey = "key-1",
                items = listOf(
                    OrderItemRequest(1L, 2L, 3L, "상품", "옵션", 2, 10000L),
                ),
                discountAmount = 1000L,
                shippingFee = 3000L,
                shippingAddress = ShippingAddressRequest(
                    "홍길동",
                    "010-1234-5678",
                    "12345",
                    "서울",
                    "101호",
                ),
            )

            val command = request.toCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.idempotencyKey).isEqualTo("key-1")
            assertThat(command.items).hasSize(1)
            assertThat(command.items[0].skuId).isEqualTo(1L)
            assertThat(command.items[0].productId).isEqualTo(2L)
            assertThat(command.items[0].brandId).isEqualTo(3L)
            assertThat(command.items[0].titleSnapshot).isEqualTo("상품")
            assertThat(command.items[0].optionSnapshot).isEqualTo("옵션")
            assertThat(command.items[0].quantity).isEqualTo(2)
            assertThat(command.items[0].unitPrice).isEqualTo(Money(10000))
            assertThat(command.discountAmount).isEqualTo(Money(1000))
            assertThat(command.shippingFee).isEqualTo(Money(3000))
            assertThat(command.shippingAddress.recipient).isEqualTo("홍길동")
            assertThat(command.shippingAddress.phone).isEqualTo("010-1234-5678")
            assertThat(command.shippingAddress.zipCode).isEqualTo("12345")
            assertThat(command.shippingAddress.address).isEqualTo("서울")
            assertThat(command.shippingAddress.addressDetail).isEqualTo("101호")
        }

        @Test
        fun `CreateOrderRequest 프로퍼티 접근 및 동등성`() {
            val address = ShippingAddressRequest("홍길동", "010-1234-5678", "12345", "서울", "101호")
            val item = OrderItemRequest(1L, 2L, 3L, "상품", "옵션", 2, 10000L)
            val request = CreateOrderRequest(
                idempotencyKey = "key-1",
                items = listOf(item),
                discountAmount = 1000L,
                shippingFee = 3000L,
                shippingAddress = address,
            )

            assertThat(request.idempotencyKey).isEqualTo("key-1")
            assertThat(request.items).hasSize(1)
            assertThat(request.discountAmount).isEqualTo(1000L)
            assertThat(request.shippingFee).isEqualTo(3000L)
            assertThat(request.shippingAddress).isEqualTo(address)

            val copy = request.copy(discountAmount = 2000L)
            assertThat(copy.discountAmount).isEqualTo(2000L)
            assertThat(request).isNotEqualTo(copy)
            assertThat(request.hashCode()).isNotEqualTo(copy.hashCode())
            assertThat(request.toString()).contains("key-1")
        }
    }

    @Nested
    @DisplayName("OrderItemRequest.toCommand")
    inner class OrderItemRequestTest {

        @Test
        fun `OrderItemRequest를 OrderItemCommand로 변환`() {
            val request = OrderItemRequest(1L, 2L, 3L, "상품", null, 5, 20000L)

            val command = request.toCommand()

            assertThat(command.skuId).isEqualTo(1L)
            assertThat(command.optionSnapshot).isNull()
            assertThat(command.quantity).isEqualTo(5)
            assertThat(command.unitPrice).isEqualTo(Money(20000))
        }

        @Test
        fun `OrderItemRequest 프로퍼티 접근 및 동등성`() {
            val request = OrderItemRequest(1L, 2L, 3L, "상품", "옵션", 2, 10000L)

            assertThat(request.skuId).isEqualTo(1L)
            assertThat(request.productId).isEqualTo(2L)
            assertThat(request.brandId).isEqualTo(3L)
            assertThat(request.titleSnapshot).isEqualTo("상품")
            assertThat(request.optionSnapshot).isEqualTo("옵션")
            assertThat(request.quantity).isEqualTo(2)
            assertThat(request.unitPrice).isEqualTo(10000L)

            val copy = request.copy(quantity = 5)
            assertThat(copy.quantity).isEqualTo(5)
            assertThat(request).isNotEqualTo(copy)
            assertThat(request.hashCode()).isNotEqualTo(copy.hashCode())
            assertThat(request.toString()).contains("상품")
        }
    }

    @Nested
    @DisplayName("ShippingAddressRequest.toCommand")
    inner class ShippingAddressRequestTest {

        @Test
        fun `ShippingAddressRequest를 ShippingAddressCommand로 변환`() {
            val request = ShippingAddressRequest("홍길동", "010-1234-5678", "12345", "서울", "101호")

            val command = request.toCommand()

            assertThat(command.recipient).isEqualTo("홍길동")
            assertThat(command.phone).isEqualTo("010-1234-5678")
            assertThat(command.zipCode).isEqualTo("12345")
            assertThat(command.address).isEqualTo("서울")
            assertThat(command.addressDetail).isEqualTo("101호")
        }

        @Test
        fun `ShippingAddressRequest 프로퍼티 접근 및 동등성`() {
            val request = ShippingAddressRequest("홍길동", "010-1234-5678", "12345", "서울", "101호")

            assertThat(request.recipient).isEqualTo("홍길동")
            assertThat(request.phone).isEqualTo("010-1234-5678")
            assertThat(request.zipCode).isEqualTo("12345")
            assertThat(request.address).isEqualTo("서울")
            assertThat(request.addressDetail).isEqualTo("101호")

            val copy = request.copy(recipient = "김철수")
            assertThat(copy.recipient).isEqualTo("김철수")
            assertThat(request).isNotEqualTo(copy)
            assertThat(request.hashCode()).isNotEqualTo(copy.hashCode())
            assertThat(request.toString()).contains("홍길동")
        }
    }

    @Nested
    @DisplayName("AddCartItemRequest.toCommand")
    inner class AddCartItemRequestTest {

        @Test
        fun `AddCartItemRequest를 AddCartItemCommand로 변환`() {
            val request = AddCartItemRequest(skuId = 100L, qty = 3, idempotencyKey = "cart-key")

            val command = request.toCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.skuId).isEqualTo(100L)
            assertThat(command.qty).isEqualTo(3)
            assertThat(command.idempotencyKey).isEqualTo("cart-key")
        }
    }

    @Nested
    @DisplayName("UpdateCartItemRequest.toCommand")
    inner class UpdateCartItemRequestTest {

        @Test
        fun `UpdateCartItemRequest를 UpdateCartItemCommand로 변환`() {
            val request = UpdateCartItemRequest(qty = 5)

            val command = request.toCommand(1L, 10L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.cartItemId).isEqualTo(10L)
            assertThat(command.qty).isEqualTo(5)
        }
    }

    @Nested
    @DisplayName("RefundOrderItemsRequest.toCommand")
    inner class RefundOrderItemsRequestTest {

        @Test
        fun `RefundOrderItemsRequest를 RefundOrderItemsCommand로 변환`() {
            val request = RefundOrderItemsRequest(itemIds = listOf(1L, 2L, 3L))

            val command = request.toCommand(10L, 1L)

            assertThat(command.orderId).isEqualTo(10L)
            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.itemIds).containsExactly(1L, 2L, 3L)
        }
    }

    @Nested
    @DisplayName("MarkOrderConfirmedCommand")
    inner class MarkOrderConfirmedCommandTest {

        @Test
        fun `MarkedConfirmedItem 생성 및 프로퍼티 접근`() {
            val item = MarkOrderConfirmedCommand.MarkedConfirmedItem(skuId = 2L, quantity = 5)

            assertThat(item.skuId).isEqualTo(2L)
            assertThat(item.quantity).isEqualTo(5)

            val same = MarkOrderConfirmedCommand.MarkedConfirmedItem(skuId = 2L, quantity = 5)
            assertThat(item).isEqualTo(same)
            assertThat(item.hashCode()).isEqualTo(same.hashCode())
            assertThat(item.toString()).contains("2")

            val command = MarkOrderConfirmedCommand(orderId = 20L, reservationId = "res-1", items = listOf(item))
            assertThat(command.orderId).isEqualTo(20L)
            assertThat(command.reservationId).isEqualTo("res-1")
            assertThat(command.items).hasSize(1)
        }
    }

    @Nested
    @DisplayName("Response DTO from 메서드")
    inner class ResponseFromTest {

        @Test
        fun `CreateOrderResponse from CreateOrderResult`() {
            val result = CreateOrderResult(1L, "ORD-1", OrderStatus.CREATED, 20000L)
            val response = CreateOrderResponse.from(result)

            assertThat(response.orderId).isEqualTo(1L)
            assertThat(response.orderNo).isEqualTo("ORD-1")
            assertThat(response.status).isEqualTo(OrderStatus.CREATED)
            assertThat(response.totalAmount).isEqualTo(20000L)
        }

        @Test
        fun `OrderResponse from OrderListResult`() {
            val result = OrderListResult(1L, "ORD-1", 1L, OrderStatus.PAID, 20000, 0, 0, 20000, "KRW", now, now)
            val response = OrderResponse.from(result)

            assertThat(response.orderId).isEqualTo(1L)
            assertThat(response.orderNo).isEqualTo("ORD-1")
            assertThat(response.status).isEqualTo(OrderStatus.PAID)
        }

        @Test
        fun `OrderDetailResponse from OrderDetailResult`() {
            val result = OrderDetailResult(
                1L, "ORD-1", 1L, OrderStatus.CONFIRMED, 20000, 0, 0, 20000, "KRW",
                "{}", null, listOf(OrderItemDetailResult(1L, 1L, 1L, 1L, "상품", null, 2, 10000, 20000)),
                now, now, now, now, null,
            )
            val response = OrderDetailResponse.from(result)

            assertThat(response.orderId).isEqualTo(1L)
            assertThat(response.items).hasSize(1)
            assertThat(response.paidAt).isNotNull()
            assertThat(response.canceledAt).isNull()
        }

        @Test
        fun `OrderItemResponse from OrderItemDetailResult`() {
            val result = OrderItemDetailResult(1L, 1L, 10L, 100L, "상품", "옵션", 2, 10000, 20000)
            val response = OrderItemResponse.from(result)

            assertThat(response.itemId).isEqualTo(1L)
            assertThat(response.skuId).isEqualTo(1L)
            assertThat(response.productId).isEqualTo(10L)
            assertThat(response.brandId).isEqualTo(100L)
            assertThat(response.titleSnapshot).isEqualTo("상품")
            assertThat(response.optionSnapshot).isEqualTo("옵션")
            assertThat(response.qty).isEqualTo(2)
            assertThat(response.unitPrice).isEqualTo(10000L)
            assertThat(response.lineAmount).isEqualTo(20000L)
        }

        @Test
        fun `RefundOrderItemsResponse from RefundOrderItemsResult`() {
            val result = RefundOrderItemsResult(1L, 30000L, listOf(1L, 2L), OrderStatus.REFUNDED)
            val response = RefundOrderItemsResponse.from(result)

            assertThat(response.orderId).isEqualTo(1L)
            assertThat(response.refundAmount).isEqualTo(30000L)
            assertThat(response.refundedItemIds).containsExactly(1L, 2L)
            assertThat(response.orderStatus).isEqualTo(OrderStatus.REFUNDED)
        }

        @Test
        fun `CartResponse from CartResult`() {
            val result = CartResult(1L, listOf(CartItemResult(1L, 100L, 2)))
            val response = CartResponse.from(result)

            assertThat(response.cartId).isEqualTo(1L)
            assertThat(response.items).hasSize(1)
        }

        @Test
        fun `CartItemResponse from CartItemResult`() {
            val result = CartItemResult(1L, 100L, 3)
            val response = CartItemResponse.from(result)

            assertThat(response.cartItemId).isEqualTo(1L)
            assertThat(response.skuId).isEqualTo(100L)
            assertThat(response.qty).isEqualTo(3)
        }
    }
}
