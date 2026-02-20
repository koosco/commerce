package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.UpdateCartItemCommand
import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.common.error.CartErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateCartItemQuantityUseCase(private val cartRepository: CartRepository) {

    @Transactional
    fun execute(command: UpdateCartItemCommand): CartItemResult {
        val cart = cartRepository.findByUserId(command.userId)
            ?: throw NotFoundException(CartErrorCode.CART_NOT_FOUND)

        val item = cart.updateItemQuantity(command.cartItemId, command.qty)

        return CartItemResult.from(item)
    }
}
