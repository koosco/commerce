package com.koosco.authservice.infra.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * HTTP 요청마다 고유한 Correlation ID를 생성하고 MDC에 설정하는 필터
 *
 * MDC(Mapped Diagnostic Context)에 다음 정보를 추가합니다:
 * - correlationId: 요청별 고유 ID (X-Correlation-ID 헤더가 있으면 사용, 없으면 생성)
 * - requestId: 요청 ID (correlationId와 동일)
 * - clientIp: 클라이언트 IP 주소
 *
 * Micrometer Tracing이 자동으로 추가하는 정보:
 * - traceId: 분산 추적 ID
 * - spanId: 현재 작업 단위 ID
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
        const val REQUEST_ID_HEADER = "X-Request-ID"
        const val MDC_CORRELATION_ID = "correlationId"
        const val MDC_REQUEST_ID = "requestId"
        const val MDC_CLIENT_IP = "clientIp"
        const val MDC_USER_ID = "userId"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            // Correlation ID 설정 (헤더에 있으면 사용, 없으면 새로 생성)
            val correlationId =
                request.getHeader(CORRELATION_ID_HEADER)
                    ?: request.getHeader(REQUEST_ID_HEADER)
                    ?: generateCorrelationId()

            // MDC에 추가
            MDC.put(MDC_CORRELATION_ID, correlationId)
            MDC.put(MDC_REQUEST_ID, correlationId)

            // 클라이언트 IP 추가
            val clientIp = extractClientIp(request)
            MDC.put(MDC_CLIENT_IP, clientIp)

            // Response 헤더에 Correlation ID 추가 (클라이언트가 추적할 수 있도록)
            response.setHeader(CORRELATION_ID_HEADER, correlationId)

            log.debug(
                "Request started - correlationId: {}, method: {}, uri: {}, clientIp: {}",
                correlationId,
                request.method,
                request.requestURI,
                clientIp,
            )

            filterChain.doFilter(request, response)

            log.debug(
                "Request completed - correlationId: {}, status: {}",
                correlationId,
                response.status,
            )
        } finally {
            // 요청 처리 후 MDC 정리 (메모리 누수 방지)
            MDC.clear()
        }
    }

    /**
     * 고유한 Correlation ID 생성
     */
    private fun generateCorrelationId(): String = UUID.randomUUID().toString()

    /**
     * 클라이언트 실제 IP 추출
     * Proxy나 Load Balancer를 거치는 경우를 고려하여 X-Forwarded-For 헤더 확인
     */
    private fun extractClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",").first().trim()
            else -> request.remoteAddr
        }
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
