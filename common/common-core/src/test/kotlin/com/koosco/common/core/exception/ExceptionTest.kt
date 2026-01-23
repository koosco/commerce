package com.koosco.common.core.exception

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.CommonErrorCode
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExceptionTest {

    @Test
    fun `BaseException should convert to ApiError correctly`() {
        val exception = BaseException(
            errorCode = CommonErrorCode.BAD_REQUEST,
            message = "Custom message",
        )

        val apiError = exception.toApiError()

        assertEquals("COMMON-400", apiError.code)
        assertEquals("잘못된 요청입니다.", apiError.message)
        assertEquals("Custom message", apiError.details)
    }

    @Test
    fun `BaseException without custom message should not include details`() {
        val exception = BaseException(CommonErrorCode.BAD_REQUEST)

        val apiError = exception.toApiError()

        assertEquals("COMMON-400", apiError.code)
        assertNull(apiError.details)
    }

    @Test
    fun `ValidationException should include field errors`() {
        val fieldErrors = listOf(
            ApiError.FieldError("email", "invalid@", "Invalid email format"),
        )
        val exception = ValidationException(
            message = "검증 오류가 발생했습니다.",
            fieldErrors = fieldErrors,
        )

        val apiError = exception.toApiError()

        assertEquals("COMMON-400-004", apiError.code)
        assertEquals(1, apiError.fieldErrors?.size)
        assertEquals("email", apiError.fieldErrors?.first()?.field)
    }

    @Test
    fun `NotFoundException should have 404 status`() {
        val exception = NotFoundException(message = "User not found")

        assertEquals(HttpStatus.NOT_FOUND, exception.errorCode.status)
        assertEquals("COMMON-404", exception.errorCode.code)
    }

    @Test
    fun `UnauthorizedException should have 401 status`() {
        val exception = UnauthorizedException(
            errorCode = CommonErrorCode.INVALID_TOKEN,
            message = "토큰이 유효하지 않습니다.",
        )

        assertEquals(HttpStatus.UNAUTHORIZED, exception.errorCode.status)
        assertEquals("COMMON-401-001", exception.errorCode.code)
    }

    @Test
    fun `ForbiddenException should have 403 status`() {
        val exception = ForbiddenException(message = "Access denied")

        assertEquals(HttpStatus.FORBIDDEN, exception.errorCode.status)
    }

    @Test
    fun `ConflictException should have 409 status`() {
        val exception = ConflictException(
            errorCode = CommonErrorCode.DUPLICATE_RESOURCE,
            message = "이메일이 이미 사용 중입니다.",
        )

        assertEquals(HttpStatus.CONFLICT, exception.errorCode.status)
        assertEquals("COMMON-409-001", exception.errorCode.code)
    }

    @Test
    fun `InternalServerException should have 500 status`() {
        val cause = RuntimeException("Database error")
        val exception = InternalServerException(
            message = "서버 오류가 발생했습니다.",
            cause = cause,
        )

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.errorCode.status)
        assertNotNull(exception.cause)
    }

    @Test
    fun `ExternalServiceException should have 502 status`() {
        val exception = ExternalServiceException(message = "결제 서비스 오류")

        assertEquals(HttpStatus.BAD_GATEWAY, exception.errorCode.status)
    }

    @Test
    fun `ServiceUnavailableException should have 503 status`() {
        val exception = ServiceUnavailableException()

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.errorCode.status)
    }
}
