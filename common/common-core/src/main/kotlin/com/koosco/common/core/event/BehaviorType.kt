package com.koosco.common.core.event

/**
 * 사용자 행동 유형을 정의하는 enum.
 * 향후 인기 상품 정렬, 개인화 추천, A/B 테스트 등에 활용.
 */
enum class BehaviorType {
    VIEW,
    CART_ADD,
    PURCHASE,
    SEARCH,
}
