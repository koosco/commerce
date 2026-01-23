package com.koosco.commonsecurity.jwt

/**
 * JWT 토큰에서 추출한 사용자 인증 정보
 * @AuthenticationPrincipal로 컨트롤러에서 주입받아 사용
 */
data class JwtUserPrincipal(val userId: String, val roles: List<String>)
