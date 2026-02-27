package com.koosco.catalogservice.application.command

/**
 * 검색 결과에서 사용자가 클릭한 상품 데이터.
 * mAP@k 계산을 위한 클릭 로그 수집에 사용된다.
 */
data class SearchClickCommand(
    val userId: Long,
    val searchQuery: String,
    val clickedProductId: Long,
    val clickPosition: Int,
    val totalResults: Int,
)
