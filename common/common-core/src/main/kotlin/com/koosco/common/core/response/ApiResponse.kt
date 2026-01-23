package com.koosco.common.core.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.ErrorCode
import java.time.Instant

/**
 * Generic API response wrapper for consistent response format across MSA services.
 *
 * @param T The type of data contained in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val timestamp: Instant = Instant.now(),
) {
    companion object {
        /**
         * Create a successful response with data.
         */
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(
            success = true,
            data = data,
        )

        /**
         * Create a successful response without data.
         */
        fun <T> success(): ApiResponse<T> = ApiResponse(
            success = true,
        )

        /**
         * Create an error response with error details.
         */
        fun <T> error(error: ApiError): ApiResponse<T> = ApiResponse(
            success = false,
            error = error,
        )

        /**
         * Create an error response from ErrorCode.
         */
        fun <T> error(errorCode: ErrorCode, message: String? = null): ApiResponse<T> = ApiResponse(
            success = false,
            error = ApiError.of(errorCode, message),
        )

        /**
         * Create an error response from ErrorCode with field errors.
         */
        fun <T> error(
            errorCode: ErrorCode,
            message: String? = null,
            fieldErrors: List<ApiError.FieldError>? = null,
        ): ApiResponse<T> = ApiResponse(
            success = false,
            error = ApiError.of(errorCode, message, fieldErrors),
        )
    }
}
