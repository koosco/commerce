package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.SearchClickCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

/**
 * 검색 결과 클릭 로그 요청.
 */
data class SearchClickRequest(
    @field:NotBlank(message = "검색 쿼리는 필수입니다.")
    val searchQuery: String,

    @field:Positive(message = "상품 ID는 양수여야 합니다.")
    val clickedProductId: Long,

    @field:Min(value = 1, message = "클릭 위치는 1 이상이어야 합니다.")
    val clickPosition: Int,

    @field:Positive(message = "총 결과 수는 양수여야 합니다.")
    val totalResults: Int,
) {
    fun toCommand(userId: Long): SearchClickCommand = SearchClickCommand(
        userId = userId,
        searchQuery = searchQuery,
        clickedProductId = clickedProductId,
        clickPosition = clickPosition,
        totalResults = totalResults,
    )
}
