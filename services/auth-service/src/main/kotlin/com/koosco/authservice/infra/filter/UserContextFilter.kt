package com.koosco.authservice.infra.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 인증된 사용자 정보를 MDC에 추가하는 필터
 *
 * SecurityContext에서 인증 정보를 가져와 MDC에 userId를 추가합니다.
 * 이를 통해 모든 로그에 사용자 정보가 자동으로 포함됩니다.
 *
 * 실행 순서: CorrelationIdFilter 다음에 실행
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class UserContextFilter : OncePerRequestFilter() {
    companion object {
        const val MDC_USER_ID = "userId"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            // SecurityContext에서 인증 정보 가져오기
            val authentication = SecurityContextHolder.getContext().authentication

            // 인증된 사용자가 있으면 MDC에 추가
            if (authentication != null && authentication.isAuthenticated) {
                val userId = extractUserId(authentication)
                if (userId != null) {
                    MDC.put(MDC_USER_ID, userId)
                }
            }

            filterChain.doFilter(request, response)
        } finally {
            // userId만 제거 (다른 MDC 값은 CorrelationIdFilter에서 정리)
            MDC.remove(MDC_USER_ID)
        }
    }

    /**
     * Authentication 객체에서 사용자 ID 추출
     *
     * - JWT의 subject (sub claim)
     * - UserDetails의 username
     * - Principal의 name
     *
     * 프로젝트의 인증 방식에 맞게 커스터마이징 필요
     */
    private fun extractUserId(authentication: org.springframework.security.core.Authentication): String? = when {
        // Anonymous 사용자는 제외
        authentication.principal == "anonymousUser" -> null

        // JWT 사용 시: Principal이 String (subject)인 경우
        authentication.principal is String -> authentication.principal as String

        // UserDetails 사용 시
        authentication.principal is org.springframework.security.core.userdetails.UserDetails -> {
            (authentication.principal as org.springframework.security.core.userdetails.UserDetails).username
        }

        // 기타 경우
        else -> authentication.name
    }

    /**
     * Actuator health check 요청은 필터링하지 않음 (성능 최적화)
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/actuator/health") ||
            path.startsWith("/actuator/prometheus")
    }
}
