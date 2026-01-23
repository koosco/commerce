package com.koosco.commonobservability.logging

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.util.*

/**
 * MDC Filter for adding distributed tracing context to logs
 * Adds trace ID, span ID, and request information to MDC
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class MdcFilter : Filter {

    companion object {
        const val TRACE_ID_KEY = "traceId"
        const val SPAN_ID_KEY = "spanId"
        const val REQUEST_URI_KEY = "requestUri"
        const val REQUEST_METHOD_KEY = "requestMethod"
        const val CLIENT_IP_KEY = "clientIp"
        const val USER_AGENT_KEY = "userAgent"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            try {
                // Add trace ID (from header or generate new)
                val traceId = request.getHeader("X-B3-TraceId")
                    ?: request.getHeader("X-Trace-Id")
                    ?: generateTraceId()
                MDC.put(TRACE_ID_KEY, traceId)

                // Add span ID (from header or generate new)
                val spanId = request.getHeader("X-B3-SpanId")
                    ?: request.getHeader("X-Span-Id")
                    ?: generateSpanId()
                MDC.put(SPAN_ID_KEY, spanId)

                // Add request information
                MDC.put(REQUEST_URI_KEY, request.requestURI)
                MDC.put(REQUEST_METHOD_KEY, request.method)
                MDC.put(CLIENT_IP_KEY, getClientIp(request))
                request.getHeader("User-Agent")?.let { MDC.put(USER_AGENT_KEY, it) }

                chain.doFilter(request, response)
            } finally {
                // Clean up MDC to prevent memory leaks in thread pools
                MDC.clear()
            }
        } else {
            chain.doFilter(request, response)
        }
    }

    private fun generateTraceId(): String = UUID.randomUUID().toString().replace("-", "")

    private fun generateSpanId(): String = UUID.randomUUID().toString().replace("-", "").substring(0, 16)

    private fun getClientIp(request: HttpServletRequest): String {
        // Check for proxy headers (common in k8s environments)
        val headers = listOf(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
        )

        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrEmpty() && ip != "unknown") {
                // X-Forwarded-For can contain multiple IPs, take the first one
                return ip.split(",").first().trim()
            }
        }

        return request.remoteAddr ?: "unknown"
    }
}
