package com.koosco.orderservice.common.error

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class CartErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_CART_REQUEST("CART-400-001", "장바구니 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CART_QUANTITY("CART-400-002", "장바구니 상품 수량이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    CART_NOT_FOUND("CART-404-001", "장바구니를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND("CART-404-002", "장바구니 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    CART_ITEM_ALREADY_EXISTS("CART-409-001", "이미 장바구니에 담긴 상품입니다.", HttpStatus.CONFLICT),
}
