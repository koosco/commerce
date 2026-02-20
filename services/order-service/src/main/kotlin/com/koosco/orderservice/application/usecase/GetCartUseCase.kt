package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.application.command.GetCartCommand
import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.application.result.CartResult
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCartUseCase(private val cartRepository: CartRepository) {

    @Transactional(readOnly = true)
    fun execute(command: GetCartCommand): CartResult {
        val cart = cartRepository.findByUserId(command.userId)
            ?: return CartResult(cartId = 0, items = emptyList())

        return CartResult.from(cart)
    }
}
