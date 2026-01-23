package com.koosco.authservice.common.logging

import org.slf4j.MDC

/**
 * MDC(Mapped Diagnostic Context) 유틸리티 클래스
 *
 * MDC에 저장된 추적 정보를 쉽게 접근할 수 있는 헬퍼 메서드 제공
 */
object MDCUtils {
    private const val CORRELATION_ID = "correlationId"
    private const val REQUEST_ID = "requestId"
    private const val TRACE_ID = "traceId"
    private const val SPAN_ID = "spanId"
    private const val CLIENT_IP = "clientIp"
    private const val USER_ID = "userId"

    /**
     * 현재 요청의 Correlation ID 반환
     */
    fun getCorrelationId(): String? = MDC.get(CORRELATION_ID)

    /**
     * 현재 요청의 Request ID 반환
     */
    fun getRequestId(): String? = MDC.get(REQUEST_ID)

    /**
     * 현재 요청의 Trace ID 반환 (Micrometer Tracing)
     */
    fun getTraceId(): String? = MDC.get(TRACE_ID)

    /**
     * 현재 요청의 Span ID 반환 (Micrometer Tracing)
     */
    fun getSpanId(): String? = MDC.get(SPAN_ID)

    /**
     * 현재 요청의 클라이언트 IP 반환
     */
    fun getClientIp(): String? = MDC.get(CLIENT_IP)

    /**
     * 현재 요청의 사용자 ID 반환
     */
    fun getUserId(): String? = MDC.get(USER_ID)

    /**
     * 모든 MDC 컨텍스트 반환
     */
    fun getAllContext(): Map<String, String> = MDC.getCopyOfContextMap() ?: emptyMap()

    /**
     * 비동기 작업을 위한 MDC 컨텍스트 복사
     *
     * 사용 예:
     * ```kotlin
     * val mdcContext = MDCUtils.captureContext()
     * CompletableFuture.supplyAsync {
     *     MDCUtils.restoreContext(mdcContext)
     *     // 비동기 작업 수행
     *     MDC.clear()
     * }
     * ```
     */
    fun captureContext(): Map<String, String>? = MDC.getCopyOfContextMap()

    /**
     * 비동기 작업에서 MDC 컨텍스트 복원
     */
    fun restoreContext(context: Map<String, String>?) {
        if (context != null) {
            MDC.setContextMap(context)
        }
    }

    /**
     * 비동기 작업 실행 시 MDC 컨텍스트를 자동으로 전달하는 헬퍼 함수
     *
     * 사용 예:
     * ```kotlin
     * MDCUtils.withMDC {
     *     // 비동기 작업 수행
     *     // MDC 컨텍스트가 자동으로 복원됨
     * }
     * ```
     */
    inline fun <T> withMDC(crossinline block: () -> T): T {
        val context = captureContext()
        return try {
            restoreContext(context)
            block()
        } finally {
            MDC.clear()
        }
    }
}
