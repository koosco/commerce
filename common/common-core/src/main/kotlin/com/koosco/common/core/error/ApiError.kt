package com.koosco.common.core.error

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Standard error structure for API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val code: String,
    val message: String,
    val details: String? = null,
    val fieldErrors: List<FieldError>? = null,
) {
    /**
     * Field-level validation error.
     */
    data class FieldError(
        val field: String,
        val value: Any?,
        val reason: String,
    )

    companion object {
        /**
         * Create ApiError from ErrorCode.
         */
        fun of(errorCode: ErrorCode, details: String? = null): ApiError = ApiError(
            code = errorCode.code,
            message = errorCode.message,
            details = details,
        )

        /**
         * Create ApiError from ErrorCode with field errors.
         */
        fun of(
            errorCode: ErrorCode,
            details: String? = null,
            fieldErrors: List<FieldError>?,
        ): ApiError = ApiError(
            code = errorCode.code,
            message = errorCode.message,
            details = details,
            fieldErrors = fieldErrors?.takeIf { it.isNotEmpty() },
        )
    }
}
