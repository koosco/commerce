package com.koosco.orderservice.application.usecase

import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.AddCartItemCommand
import com.koosco.orderservice.application.command.ClearCartCommand
import com.koosco.orderservice.application.command.GetCartCommand
import com.koosco.orderservice.application.command.RemoveCartItemCommand
import com.koosco.orderservice.application.command.UpdateCartItemCommand
import com.koosco.orderservice.application.port.CartIdempotencyRepository
import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.domain.entity.Cart
import com.koosco.orderservice.domain.entity.CartIdempotency
import com.koosco.orderservice.domain.entity.CartItem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@DisplayName("Cart UseCase 테스트")
class CartUseCaseTest {

    private val cartRepository: CartRepository = mock()
    private val cartIdempotencyRepository: CartIdempotencyRepository = mock()

    private fun createTestCart(userId: Long = 1L, cartId: Long = 1L): Cart {
        val cart = Cart.create(userId)
        val idField = Cart::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(cart, cartId)
        return cart
    }

    private fun addItemToCart(cart: Cart, skuId: Long, qty: Int, itemId: Long): CartItem {
        val item = cart.addItem(skuId, qty)
        val idField = CartItem::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(item, itemId)
        return item
    }

    private fun stubSaveAssigningItemIds(cart: Cart, startId: Long = 1L) {
        whenever(cartRepository.save(any())).thenAnswer { invocation ->
            val saved = invocation.getArgument<Cart>(0)
            val idField = CartItem::class.java.getDeclaredField("id")
            idField.isAccessible = true
            var nextId = startId
            saved.items.forEach { item ->
                if (item.id == null) {
                    idField.set(item, nextId++)
                }
            }
            saved
        }
    }

    @Nested
    @DisplayName("GetCartUseCase")
    inner class GetCartUseCaseTest {

        private val useCase = GetCartUseCase(cartRepository)

        @Test
        fun `장바구니 조회 성공`() {
            val cart = createTestCart()
            addItemToCart(cart, 100L, 2, 1L)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)

            val result = useCase.execute(GetCartCommand(1L))

            assertThat(result.cartId).isEqualTo(1L)
            assertThat(result.items).hasSize(1)
        }

        @Test
        fun `장바구니가 없으면 빈 장바구니를 반환한다`() {
            whenever(cartRepository.findByUserId(1L)).thenReturn(null)

            val result = useCase.execute(GetCartCommand(1L))

            assertThat(result.cartId).isEqualTo(0L)
            assertThat(result.items).isEmpty()
        }
    }

    @Nested
    @DisplayName("AddCartItemUseCase")
    inner class AddCartItemUseCaseTest {

        private val useCase = AddCartItemUseCase(cartRepository, cartIdempotencyRepository)

        @Test
        fun `장바구니에 상품 추가 성공 - 기존 장바구니 존재`() {
            val cart = createTestCart()
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)
            stubSaveAssigningItemIds(cart)

            val command = AddCartItemCommand(userId = 1L, skuId = 100L, qty = 2)
            val result = useCase.execute(command)

            assertThat(result.skuId).isEqualTo(100L)
            assertThat(result.qty).isEqualTo(2)
        }

        @Test
        fun `장바구니가 없으면 새로 생성한다`() {
            val newCart = createTestCart()
            whenever(cartRepository.findByUserId(1L)).thenReturn(null)
            stubSaveAssigningItemIds(newCart)

            val command = AddCartItemCommand(userId = 1L, skuId = 100L, qty = 2)
            val result = useCase.execute(command)

            assertThat(result.skuId).isEqualTo(100L)
        }

        @Test
        fun `멱등성 키가 있고 기존 아이템이 존재하면 기존 아이템을 반환한다`() {
            val cart = createTestCart()
            val item = addItemToCart(cart, 100L, 2, 5L)
            val idempotency = CartIdempotency.create(1L, "cart-key", 5L)

            whenever(cartIdempotencyRepository.findByUserIdAndIdempotencyKey(1L, "cart-key"))
                .thenReturn(idempotency)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)

            val command = AddCartItemCommand(userId = 1L, skuId = 100L, qty = 2, idempotencyKey = "cart-key")
            val result = useCase.execute(command)

            assertThat(result.cartItemId).isEqualTo(5L)
            verify(cartRepository, never()).save(any())
        }

        @Test
        fun `멱등성 키가 있지만 아이템이 없으면 새로 추가한다`() {
            val cart = createTestCart()
            val idempotency = CartIdempotency.create(1L, "cart-key", 999L)

            whenever(cartIdempotencyRepository.findByUserIdAndIdempotencyKey(1L, "cart-key"))
                .thenReturn(idempotency)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)
            stubSaveAssigningItemIds(cart)

            val command = AddCartItemCommand(userId = 1L, skuId = 100L, qty = 2, idempotencyKey = "cart-key")
            val result = useCase.execute(command)

            assertThat(result.skuId).isEqualTo(100L)
        }
    }

    @Nested
    @DisplayName("UpdateCartItemQuantityUseCase")
    inner class UpdateCartItemQuantityUseCaseTest {

        private val useCase = UpdateCartItemQuantityUseCase(cartRepository)

        @Test
        fun `장바구니 상품 수량 변경 성공`() {
            val cart = createTestCart()
            addItemToCart(cart, 100L, 2, 1L)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)

            val command = UpdateCartItemCommand(userId = 1L, cartItemId = 1L, qty = 5)
            val result = useCase.execute(command)

            assertThat(result.qty).isEqualTo(5)
        }

        @Test
        fun `장바구니가 없으면 예외가 발생한다`() {
            whenever(cartRepository.findByUserId(1L)).thenReturn(null)

            val command = UpdateCartItemCommand(userId = 1L, cartItemId = 1L, qty = 5)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("RemoveCartItemUseCase")
    inner class RemoveCartItemUseCaseTest {

        private val useCase = RemoveCartItemUseCase(cartRepository)

        @Test
        fun `장바구니 상품 삭제 성공`() {
            val cart = createTestCart()
            addItemToCart(cart, 100L, 2, 1L)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)

            val command = RemoveCartItemCommand(userId = 1L, cartItemId = 1L)
            useCase.execute(command)

            assertThat(cart.items).isEmpty()
        }

        @Test
        fun `장바구니가 없으면 예외가 발생한다`() {
            whenever(cartRepository.findByUserId(1L)).thenReturn(null)

            val command = RemoveCartItemCommand(userId = 1L, cartItemId = 1L)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("ClearCartUseCase")
    inner class ClearCartUseCaseTest {

        private val useCase = ClearCartUseCase(cartRepository)

        @Test
        fun `장바구니 비우기 성공`() {
            val cart = createTestCart()
            addItemToCart(cart, 100L, 2, 1L)
            addItemToCart(cart, 200L, 1, 2L)
            whenever(cartRepository.findByUserId(1L)).thenReturn(cart)

            val command = ClearCartCommand(userId = 1L)
            useCase.execute(command)

            assertThat(cart.items).isEmpty()
        }

        @Test
        fun `장바구니가 없으면 예외가 발생한다`() {
            whenever(cartRepository.findByUserId(1L)).thenReturn(null)

            val command = ClearCartCommand(userId = 1L)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
