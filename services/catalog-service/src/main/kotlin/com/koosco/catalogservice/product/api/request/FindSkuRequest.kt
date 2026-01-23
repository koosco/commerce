package com.koosco.catalogservice.product.api.request

import jakarta.validation.constraints.NotEmpty

/**
 * SKU 조회 요청
 * - 사용자가 선택한 옵션 조합으로 SKU를 찾기 위한 요청
 */
data class FindSkuRequest(
    @field:NotEmpty(message = "옵션을 선택해주세요")
    val options: Map<String, String> = emptyMap(),
) {
    /**
     * 예시:
     * {
     *   "options": {
     *     "색상": "빨강",
     *     "사이즈": "M"
     *   }
     * }
     */
}
