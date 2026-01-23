package com.koosco.inventoryservice.common

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Inventory service specific error codes.
 */
enum class InventoryErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {

    // 400 Bad Request
    INVALID_QUANTITY("INVENTORY-400-001", "수량이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_ID("INVENTORY-400-002", "상품 ID가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    STOCK_ADJUST_NOT_ALLOWED("INVENTORY-400-003", "재고 조정이 허용되지 않습니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    PRODUCT_NOT_FOUND("INVENTORY-404-001", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVENTORY_NOT_FOUND("INVENTORY-404-002", "재고 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    NOT_ENOUGH_STOCK("INVENTORY-409-001", "재고가 부족합니다.", HttpStatus.CONFLICT),
    OUT_OF_STOCK("INVENTORY-409-002", "재고가 없습니다.", HttpStatus.CONFLICT),
    INVENTORY_ALREADY_EXISTS("INVENTORY-409-003", "이미 존재하는 재고 정보입니다.", HttpStatus.CONFLICT),
}
