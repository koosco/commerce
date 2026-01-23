package com.koosco.authservice.common

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Auth service specific error codes.
 */
enum class AuthErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_PROVIDER("AUTH-400-001", "지원하지 않는 인증 제공자입니다.", HttpStatus.BAD_REQUEST),
    INVALID_AUTH_CODE("AUTH-400-002", "잘못되거나 만료된 인증 코드입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_FORMAT("AUTH-400-003", "토큰 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SIGNATURE("AUTH-400-004", "토큰 서명이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("AUTH-400-005", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),
    MISSING_AUTH_HEADER("AUTH-400-006", "Authorization 헤더가 누락되었습니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    ACCESS_TOKEN_EXPIRED("AUTH-401-001", "액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("AUTH-401-002", "리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("AUTH-401-003", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN("AUTH-403-001", "해당 리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 404 Not Found
    PROVIDER_USER_NOT_FOUND("AUTH-404-001", "해당 제공자의 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TOKEN_NOT_FOUND("AUTH-404-002", "토큰 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    TOKEN_REISSUE_CONFLICT("AUTH-409-001", "토큰 재발급 요청이 충돌하였습니다.", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    TOKEN_GENERATION_FAILED(
        "AUTH-500-001",
        "토큰 생성 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    PROVIDER_COMMUNICATION_FAILED(
        "AUTH-500-002",
        "외부 인증 제공자와의 통신 중 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
}
