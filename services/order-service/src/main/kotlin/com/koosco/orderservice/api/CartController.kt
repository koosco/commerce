package com.koosco.orderservice.api

import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import com.koosco.orderservice.application.command.ClearCartCommand
import com.koosco.orderservice.application.command.GetCartCommand
import com.koosco.orderservice.application.command.RemoveCartItemCommand
import com.koosco.orderservice.application.usecase.AddCartItemUseCase
import com.koosco.orderservice.application.usecase.ClearCartUseCase
import com.koosco.orderservice.application.usecase.GetCartUseCase
import com.koosco.orderservice.application.usecase.RemoveCartItemUseCase
import com.koosco.orderservice.application.usecase.UpdateCartItemQuantityUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/carts/me")
class CartController(
    private val getCartUseCase: GetCartUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
) {

    @GetMapping
    fun getCart(@AuthId userId: Long): ApiResponse<CartResponse> {
        val result = getCartUseCase.execute(GetCartCommand(userId))
        return ApiResponse.success(CartResponse.from(result))
    }

    @PostMapping("/items")
    fun addItem(@AuthId userId: Long, @Valid @RequestBody request: AddCartItemRequest): ApiResponse<CartItemResponse> {
        val result = addCartItemUseCase.execute(request.toCommand(userId))
        return ApiResponse.success(CartItemResponse.from(result))
    }

    @PatchMapping("/items/{cartItemId}")
    fun updateItemQuantity(
        @AuthId userId: Long,
        @PathVariable cartItemId: Long,
        @Valid @RequestBody request: UpdateCartItemRequest,
    ): ApiResponse<CartItemResponse> {
        val result = updateCartItemQuantityUseCase.execute(request.toCommand(userId, cartItemId))
        return ApiResponse.success(CartItemResponse.from(result))
    }

    @DeleteMapping("/items/{cartItemId}")
    fun removeItem(@AuthId userId: Long, @PathVariable cartItemId: Long): ApiResponse<Unit> {
        removeCartItemUseCase.execute(RemoveCartItemCommand(userId, cartItemId))
        return ApiResponse.success(Unit)
    }

    @DeleteMapping("/items")
    fun clearCart(@AuthId userId: Long): ApiResponse<Unit> {
        clearCartUseCase.execute(ClearCartCommand(userId))
        return ApiResponse.success(Unit)
    }
}
