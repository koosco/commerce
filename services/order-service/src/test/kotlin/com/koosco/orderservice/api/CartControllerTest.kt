package com.koosco.orderservice.api

import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.application.result.CartResult
import com.koosco.orderservice.application.usecase.AddCartItemUseCase
import com.koosco.orderservice.application.usecase.ClearCartUseCase
import com.koosco.orderservice.application.usecase.GetCartUseCase
import com.koosco.orderservice.application.usecase.RemoveCartItemUseCase
import com.koosco.orderservice.application.usecase.UpdateCartItemQuantityUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@DisplayName("CartController 테스트")
class CartControllerTest {

    private val getCartUseCase: GetCartUseCase = mock()
    private val addCartItemUseCase: AddCartItemUseCase = mock()
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase = mock()
    private val removeCartItemUseCase: RemoveCartItemUseCase = mock()
    private val clearCartUseCase: ClearCartUseCase = mock()

    private val controller = CartController(
        getCartUseCase,
        addCartItemUseCase,
        updateCartItemQuantityUseCase,
        removeCartItemUseCase,
        clearCartUseCase,
    )

    @Nested
    @DisplayName("GET /api/carts/me - 장바구니 조회")
    inner class GetCart {

        @Test
        fun `장바구니 조회 성공`() {
            val cartResult = CartResult(
                cartId = 1L,
                items = listOf(CartItemResult(1L, 100L, 2)),
            )
            whenever(getCartUseCase.execute(any())).thenReturn(cartResult)

            val response = controller.getCart(1L)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.cartId).isEqualTo(1L)
            assertThat(response.data!!.items).hasSize(1)
            assertThat(response.data!!.items[0].skuId).isEqualTo(100L)
        }
    }

    @Nested
    @DisplayName("POST /api/carts/me/items - 장바구니 상품 추가")
    inner class AddItem {

        @Test
        fun `장바구니 상품 추가 성공`() {
            val request = AddCartItemRequest(skuId = 100L, qty = 2)
            val cartItemResult = CartItemResult(1L, 100L, 2)
            whenever(addCartItemUseCase.execute(any())).thenReturn(cartItemResult)

            val response = controller.addItem(1L, request)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.cartItemId).isEqualTo(1L)
            assertThat(response.data!!.skuId).isEqualTo(100L)
            assertThat(response.data!!.qty).isEqualTo(2)
        }
    }

    @Nested
    @DisplayName("PATCH /api/carts/me/items/{cartItemId} - 수량 변경")
    inner class UpdateItemQuantity {

        @Test
        fun `장바구니 상품 수량 변경 성공`() {
            val request = UpdateCartItemRequest(qty = 5)
            val cartItemResult = CartItemResult(1L, 100L, 5)
            whenever(updateCartItemQuantityUseCase.execute(any())).thenReturn(cartItemResult)

            val response = controller.updateItemQuantity(1L, 1L, request)

            assertThat(response.success).isTrue()
            assertThat(response.data!!.qty).isEqualTo(5)
        }
    }

    @Nested
    @DisplayName("DELETE /api/carts/me/items/{cartItemId} - 상품 삭제")
    inner class RemoveItem {

        @Test
        fun `장바구니 상품 삭제 성공`() {
            val response = controller.removeItem(1L, 1L)

            assertThat(response.success).isTrue()
            verify(removeCartItemUseCase).execute(any())
        }
    }

    @Nested
    @DisplayName("DELETE /api/carts/me/items - 장바구니 비우기")
    inner class ClearCart {

        @Test
        fun `장바구니 비우기 성공`() {
            val response = controller.clearCart(1L)

            assertThat(response.success).isTrue()
            verify(clearCartUseCase).execute(any())
        }
    }
}
