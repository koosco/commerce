package com.koosco.userservice.common

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class MemberErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_PASSWORD_FORMAT("MEMBER-400-001", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("MEMBER-400-002", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("MEMBER-400-003", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    INVALID_CREDENTIALS("MEMBER-401-001", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 404 Not Found
    MEMBER_NOT_FOUND("MEMBER-404-001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    EMAIL_ALREADY_EXISTS("MEMBER-409-001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    MEMBER_ALREADY_WITHDRAWN("MEMBER-409-002", "이미 탈퇴한 사용자입니다.", HttpStatus.CONFLICT),
    MEMBER_ALREADY_LOCKED("MEMBER-409-003", "이미 잠긴 사용자입니다.", HttpStatus.CONFLICT),
}
