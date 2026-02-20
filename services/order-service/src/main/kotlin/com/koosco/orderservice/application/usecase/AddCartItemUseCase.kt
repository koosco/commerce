package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.application.command.AddCartItemCommand
import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.domain.entity.Cart
import org.springframework.transaction.annotation.Transactional

@UseCase
class AddCartItemUseCase(private val cartRepository: CartRepository) {

    @Transactional
    fun execute(command: AddCartItemCommand): CartItemResult {
        val cart = cartRepository.findByUserId(command.userId)
            ?: cartRepository.save(Cart.create(command.userId))

        val item = cart.addItem(command.skuId, command.qty)
        cartRepository.save(cart)

        return CartItemResult.from(item)
    }
}
