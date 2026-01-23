package com.koosco.common.core.exception

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.CommonErrorCode
import com.koosco.common.core.error.ErrorCode

/**
 * Base exception class for all application exceptions.
 * All domain-specific exceptions should extend this class.
 */
open class BaseException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
    val fieldErrors: List<ApiError.FieldError>? = null,
) : RuntimeException(message, cause) {

    /**
     * Convert to ApiError for response.
     */
    fun toApiError(): ApiError = ApiError.of(
        errorCode = errorCode,
        details = if (message != errorCode.message) message else null,
        fieldErrors = fieldErrors,
    )
}

/**
 * Exception for bad request errors (400).
 */
open class BadRequestException(
    errorCode: ErrorCode = CommonErrorCode.BAD_REQUEST,
    message: String = errorCode.message,
    cause: Throwable? = null,
    fieldErrors: List<ApiError.FieldError>? = null,
) : BaseException(errorCode, message, cause, fieldErrors)

/**
 * Exception for validation errors (400).
 */
class ValidationException(
    message: String = CommonErrorCode.VALIDATION_ERROR.message,
    fieldErrors: List<ApiError.FieldError>? = null,
    cause: Throwable? = null,
) : BadRequestException(CommonErrorCode.VALIDATION_ERROR, message, cause, fieldErrors)

/**
 * Exception for unauthorized errors (401).
 */
open class UnauthorizedException(
    errorCode: ErrorCode = CommonErrorCode.UNAUTHORIZED,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for forbidden errors (403).
 */
open class ForbiddenException(
    errorCode: ErrorCode = CommonErrorCode.FORBIDDEN,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for not found errors (404).
 */
open class NotFoundException(
    errorCode: ErrorCode = CommonErrorCode.NOT_FOUND,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for conflict errors (409).
 */
open class ConflictException(
    errorCode: ErrorCode = CommonErrorCode.CONFLICT,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for internal server errors (500).
 */
open class InternalServerException(
    errorCode: ErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for external service errors (502).
 */
open class ExternalServiceException(
    errorCode: ErrorCode = CommonErrorCode.EXTERNAL_SERVICE_ERROR,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)

/**
 * Exception for service unavailable errors (503).
 */
open class ServiceUnavailableException(
    errorCode: ErrorCode = CommonErrorCode.SERVICE_UNAVAILABLE,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : BaseException(errorCode, message, cause)
