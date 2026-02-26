package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.entity.Promotion

/**
 * 활성 프로모션 목록에서 최종 할인가를 결정하는 도메인 서비스.
 *
 * 우선순위가 가장 높은(priority 값이 가장 낮은) 프로모션의 할인가를 반환한다.
 * 활성 프로모션이 없으면 null을 반환하여 원래 상품 가격을 사용하도록 한다.
 */
object PromotionPriceResolver {

    fun resolve(activePromotions: List<Promotion>): Long? {
        if (activePromotions.isEmpty()) return null
        return activePromotions.minBy { it.priority }.discountPrice
    }
}
