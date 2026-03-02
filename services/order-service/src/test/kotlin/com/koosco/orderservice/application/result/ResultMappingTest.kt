package com.koosco.orderservice.application.result

import com.koosco.orderservice.domain.entity.Cart
import com.koosco.orderservice.domain.entity.CartItem
import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.entity.OrderItem
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Result 매핑 테스트")
class ResultMappingTest {

    private fun createTestOrder(): Order {
        val specs = listOf(
            OrderItemSpec(1L, 10L, 100L, "상품1", "옵션A", 2, Money(10000)),
        )
        val amount = OrderAmount.from(specs, Money(1000), Money(3000))
        val order = Order.create(
            orderNo = "ORD-RESULT-TEST",
            userId = 1L,
            itemSpecs = specs,
            amount = amount,
            shippingAddressSnapshot = "{\"address\":\"서울\"}",
            pricingSnapshot = "{\"total\":22000}",
        )
        setId(order, Order::class.java, 1L)
        order.items.forEachIndexed { i, item ->
            setId(item, OrderItem::class.java, (i + 1).toLong())
        }
        return order
    }

    private fun setId(obj: Any, clazz: Class<*>, id: Long) {
        val field = clazz.getDeclaredField("id")
        field.isAccessible = true
        field.set(obj, id)
    }

    @Nested
    @DisplayName("OrderListResult.from")
    inner class OrderListResultTest {

        @Test
        fun `Order에서 OrderListResult로 변환`() {
            val order = createTestOrder()
            val result = OrderListResult.from(order)

            assertThat(result.orderId).isEqualTo(1L)
            assertThat(result.orderNo).isEqualTo("ORD-RESULT-TEST")
            assertThat(result.userId).isEqualTo(1L)
            assertThat(result.status).isEqualTo(OrderStatus.CREATED)
            assertThat(result.subtotalAmount).isEqualTo(20000L)
            assertThat(result.discountAmount).isEqualTo(1000L)
            assertThat(result.shippingFee).isEqualTo(3000L)
            assertThat(result.totalAmount).isEqualTo(22000L)
            assertThat(result.currency).isEqualTo("KRW")
        }
    }

    @Nested
    @DisplayName("OrderDetailResult.from")
    inner class OrderDetailResultTest {

        @Test
        fun `Order에서 OrderDetailResult로 변환`() {
            val order = createTestOrder()
            val result = OrderDetailResult.from(order)

            assertThat(result.orderId).isEqualTo(1L)
            assertThat(result.shippingAddressSnapshot).isEqualTo("{\"address\":\"서울\"}")
            assertThat(result.pricingSnapshot).isEqualTo("{\"total\":22000}")
            assertThat(result.items).hasSize(1)
            assertThat(result.placedAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("OrderItemDetailResult.from")
    inner class OrderItemDetailResultTest {

        @Test
        fun `OrderItem에서 OrderItemDetailResult로 변환`() {
            val order = createTestOrder()
            val item = order.items[0]
            val result = OrderItemDetailResult.from(item)

            assertThat(result.itemId).isEqualTo(1L)
            assertThat(result.skuId).isEqualTo(1L)
            assertThat(result.productId).isEqualTo(10L)
            assertThat(result.brandId).isEqualTo(100L)
            assertThat(result.titleSnapshot).isEqualTo("상품1")
            assertThat(result.optionSnapshot).isEqualTo("옵션A")
            assertThat(result.qty).isEqualTo(2)
            assertThat(result.unitPrice).isEqualTo(10000L)
            assertThat(result.lineAmount).isEqualTo(20000L)
        }
    }

    @Nested
    @DisplayName("CartResult.from")
    inner class CartResultTest {

        @Test
        fun `Cart에서 CartResult로 변환`() {
            val cart = Cart.create(1L)
            setId(cart, Cart::class.java, 1L)
            val item = cart.addItem(100L, 3)
            setId(item, CartItem::class.java, 10L)

            val result = CartResult.from(cart)

            assertThat(result.cartId).isEqualTo(1L)
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].cartItemId).isEqualTo(10L)
            assertThat(result.items[0].skuId).isEqualTo(100L)
            assertThat(result.items[0].qty).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("CartItemResult.from")
    inner class CartItemResultTest {

        @Test
        fun `CartItem에서 CartItemResult로 변환`() {
            val cart = Cart.create(1L)
            val item = cart.addItem(100L, 5)
            setId(item, CartItem::class.java, 7L)

            val result = CartItemResult.from(item)

            assertThat(result.cartItemId).isEqualTo(7L)
            assertThat(result.skuId).isEqualTo(100L)
            assertThat(result.qty).isEqualTo(5)
        }
    }
}
