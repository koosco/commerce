package com.koosco.orderservice.domain.entity

import com.koosco.common.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Cart 도메인 테스트")
class CartTest {

    private fun createCart(userId: Long = 1L): Cart = Cart.create(userId)

    private fun setCartItemId(item: CartItem, id: Long) {
        val field = CartItem::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.set(item, id)
    }

    @Nested
    @DisplayName("Cart.create")
    inner class Create {

        @Test
        fun `장바구니 생성 시 빈 아이템 목록으로 생성된다`() {
            val cart = createCart(1L)

            assertThat(cart.userId).isEqualTo(1L)
            assertThat(cart.items).isEmpty()
        }
    }

    @Nested
    @DisplayName("addItem")
    inner class AddItem {

        @Test
        fun `새로운 상품을 장바구니에 추가한다`() {
            val cart = createCart()

            val item = cart.addItem(skuId = 100L, qty = 2)

            assertThat(cart.items).hasSize(1)
            assertThat(item.skuId).isEqualTo(100L)
            assertThat(item.qty).isEqualTo(2)
        }

        @Test
        fun `동일한 상품 추가 시 수량이 합산된다`() {
            val cart = createCart()
            cart.addItem(skuId = 100L, qty = 2)

            val item = cart.addItem(skuId = 100L, qty = 3)

            assertThat(cart.items).hasSize(1)
            assertThat(item.qty).isEqualTo(5)
        }

        @Test
        fun `수량이 0 이하이면 예외가 발생한다`() {
            val cart = createCart()

            assertThatThrownBy { cart.addItem(skuId = 100L, qty = 0) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `수량이 음수이면 예외가 발생한다`() {
            val cart = createCart()

            assertThatThrownBy { cart.addItem(skuId = 100L, qty = -1) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("updateItemQuantity")
    inner class UpdateItemQuantity {

        @Test
        fun `장바구니 상품 수량을 변경한다`() {
            val cart = createCart()
            val item = cart.addItem(skuId = 100L, qty = 2)
            setCartItemId(item, 1L)

            val updated = cart.updateItemQuantity(cartItemId = 1L, qty = 5)

            assertThat(updated.qty).isEqualTo(5)
        }

        @Test
        fun `수량이 0 이하이면 예외가 발생한다`() {
            val cart = createCart()
            val item = cart.addItem(skuId = 100L, qty = 2)
            setCartItemId(item, 1L)

            assertThatThrownBy { cart.updateItemQuantity(cartItemId = 1L, qty = 0) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `존재하지 않는 아이템 수량 변경 시 예외가 발생한다`() {
            val cart = createCart()

            assertThatThrownBy { cart.updateItemQuantity(cartItemId = 999L, qty = 1) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("removeItem")
    inner class RemoveItem {

        @Test
        fun `장바구니 상품을 삭제한다`() {
            val cart = createCart()
            val item = cart.addItem(skuId = 100L, qty = 2)
            setCartItemId(item, 1L)

            cart.removeItem(cartItemId = 1L)

            assertThat(cart.items).isEmpty()
        }

        @Test
        fun `존재하지 않는 아이템 삭제 시 예외가 발생한다`() {
            val cart = createCart()

            assertThatThrownBy { cart.removeItem(cartItemId = 999L) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("clear")
    inner class Clear {

        @Test
        fun `장바구니를 비운다`() {
            val cart = createCart()
            cart.addItem(skuId = 100L, qty = 2)
            cart.addItem(skuId = 200L, qty = 1)

            cart.clear()

            assertThat(cart.items).isEmpty()
        }

        @Test
        fun `빈 장바구니를 비워도 예외가 발생하지 않는다`() {
            val cart = createCart()

            cart.clear()

            assertThat(cart.items).isEmpty()
        }
    }
}
