package com.koosco.common.core.error

import org.springframework.http.HttpStatus

/**
 * Standard error codes for consistent error handling across MSA services.
 * Services can extend this interface to define domain-specific error codes.
 */
interface ErrorCode {
    val code: String
    val message: String
    val status: HttpStatus
}

/**
 * Common error codes shared across all services.
 */
enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {

    // 400 Bad Request
    BAD_REQUEST("COMMON-400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("COMMON-400-001", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE("COMMON-400-002", "잘못된 형식의 값입니다.", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("COMMON-400-003", "필수 파라미터가 누락되었습니다.", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("COMMON-400-004", "유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHORIZED("COMMON-401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("COMMON-401-001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("COMMON-401-002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN("COMMON-403", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    ACCESS_DENIED("COMMON-403-001", "해당 리소스에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    // 404 Not Found
    NOT_FOUND("COMMON-404", "대상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND("COMMON-404-001", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED("COMMON-405", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

    // 409 Conflict
    CONFLICT("COMMON-409", "요청 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    DUPLICATE_RESOURCE("COMMON-409-001", "이미 존재하는 리소스입니다.", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("COMMON-500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_ERROR("COMMON-500-001", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 502 Bad Gateway
    BAD_GATEWAY("COMMON-502", "외부 서비스 연결에 문제가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    EXTERNAL_SERVICE_ERROR("COMMON-502-001", "외부 서비스에서 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),

    // 503 Service Unavailable
    SERVICE_UNAVAILABLE("COMMON-503", "현재 서비스를 이용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
}
