package com.koosco.userservice.common

import com.koosco.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * User service specific error codes.
 */
enum class UserErrorCode(override val code: String, override val message: String, override val status: HttpStatus) :
    ErrorCode {

    // 400 Bad Request
    INVALID_PASSWORD_FORMAT("USER-400-001", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("USER-400-002", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("USER-400-003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    INVALID_CREDENTIALS("USER-401-001", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 404 Not Found
    USER_NOT_FOUND("USER-404-001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    EMAIL_ALREADY_EXISTS("USER-409-001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER-409-002", "이미 사용 중인 사용자명입니다.", HttpStatus.CONFLICT),
    USER_ALREADY_DELETED("USER-409-003", "이미 삭제된 사용자입니다.", HttpStatus.CONFLICT),
}
