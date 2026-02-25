package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.application.command.AddCartItemCommand
import com.koosco.orderservice.application.port.CartIdempotencyRepository
import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.domain.entity.Cart
import com.koosco.orderservice.domain.entity.CartIdempotency
import org.springframework.transaction.annotation.Transactional

@UseCase
class AddCartItemUseCase(
    private val cartRepository: CartRepository,
    private val cartIdempotencyRepository: CartIdempotencyRepository,
) {

    @Transactional
    fun execute(command: AddCartItemCommand): CartItemResult {
        if (command.idempotencyKey != null) {
            val existing = cartIdempotencyRepository.findByUserIdAndIdempotencyKey(
                command.userId,
                command.idempotencyKey,
            )
            if (existing != null) {
                val cart = cartRepository.findByUserId(command.userId)
                val item = cart?.items?.find { it.id == existing.cartItemId }
                if (item != null) return CartItemResult.from(item)
            }
        }

        val cart = cartRepository.findByUserId(command.userId)
            ?: cartRepository.save(Cart.create(command.userId))

        val item = cart.addItem(command.skuId, command.qty)
        cartRepository.save(cart)

        if (command.idempotencyKey != null) {
            cartIdempotencyRepository.save(
                CartIdempotency.create(command.userId, command.idempotencyKey, item.id!!),
            )
        }

        return CartItemResult.from(item)
    }
}
