package com.koosco.catalogservice.common.error

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Catalog service specific error codes.
 */
enum class CatalogErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_CATEGORY_ID("CATALOG-400-001", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_ID("CATALOG-400-002", "유효하지 않은 상품 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SORT_OPTION("CATALOG-400-003", "지원하지 않는 정렬 옵션입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_RANGE("CATALOG-400-004", "가격 범위가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SEARCH_QUERY("CATALOG-400-005", "검색어 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TRANSITION("CATALOG-400-006", "허용되지 않는 상태 전이입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_READY_FOR_ACTIVATION("CATALOG-400-007", "상품이 활성화 조건을 충족하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHORIZED("CATALOG-401-001", "상품 정보를 조회하려면 인증이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN("CATALOG-403-001", "해당 상품 또는 카테고리에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 404 Not Found
    PRODUCT_NOT_FOUND("CATALOG-404-001", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("CATALOG-404-002", "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_IMAGE_NOT_FOUND("CATALOG-404-003", "상품 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OPTION_NOT_FOUND("CATALOG-404-004", "상품 옵션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    BRAND_NOT_FOUND("CATALOG-404-005", "브랜드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ATTRIBUTE_NOT_FOUND("CATALOG-404-008", "속성을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DISCOUNT_POLICY_NOT_FOUND("CATALOG-404-009", "할인 정책을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 404 Not Found (Content)
    REVIEW_NOT_FOUND("CATALOG-404-006", "리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SNAP_NOT_FOUND("CATALOG-404-007", "스냅을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_FOUND("CATALOG-404-008", "프로모션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    PRODUCT_NAME_CONFLICT("CATALOG-409-001", "이미 존재하는 상품명입니다.", HttpStatus.CONFLICT),
    CATEGORY_NAME_CONFLICT("CATALOG-409-002", "이미 존재하는 카테고리명입니다.", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    PRODUCT_CREATION_FAILED(
        "CATALOG-500-001",
        "상품 생성 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    CATEGORY_CREATION_FAILED(
        "CATALOG-500-002",
        "카테고리 생성 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    IMAGE_UPLOAD_FAILED(
        "CATALOG-500-003",
        "이미지 업로드 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    EXTERNAL_SERVICE_COMMUNICATION_FAILED(
        "CATALOG-500-004",
        "외부 서비스와의 통신 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
}
