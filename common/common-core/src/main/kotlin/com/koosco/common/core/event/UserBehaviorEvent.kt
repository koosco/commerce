package com.koosco.common.core.event

/**
 * 사용자 행동 이벤트 데이터.
 * 각 서비스에서 발행하여 사용자 행동 데이터를 수집하는 파이프라인의 기반.
 */
data class UserBehaviorEvent(
    val userId: Long,
    val behaviorType: BehaviorType,
    val productId: Long?,
    val searchQuery: String?,
    val metadata: Map<String, String> = emptyMap(),
)
