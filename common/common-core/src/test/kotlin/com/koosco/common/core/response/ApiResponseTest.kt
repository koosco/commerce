package com.koosco.common.core.response

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.CommonErrorCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiResponseTest {

    @Test
    fun `success with data should create successful response`() {
        val data = mapOf("name" to "test", "value" to 123)

        val response = ApiResponse.success(data)

        assertTrue(response.success)
        assertEquals(data, response.data)
        assertNull(response.error)
        assertNotNull(response.timestamp)
    }

    @Test
    fun `success without data should create successful response`() {
        val response = ApiResponse.success<Unit>()

        assertTrue(response.success)
        assertNull(response.data)
        assertNull(response.error)
    }

    @Test
    fun `error with ApiError should create error response`() {
        val apiError = ApiError.of(CommonErrorCode.BAD_REQUEST, "Test error")

        val response = ApiResponse.error<Any>(apiError)

        assertFalse(response.success)
        assertNull(response.data)
        assertNotNull(response.error)
        assertEquals("COMMON-400", response.error?.code)
    }

    @Test
    fun `error with ErrorCode should create error response`() {
        val response = ApiResponse.error<Any>(CommonErrorCode.NOT_FOUND, "Resource not found")

        assertFalse(response.success)
        assertNull(response.data)
        assertNotNull(response.error)
        assertEquals("COMMON-404", response.error?.code)
        assertEquals("Resource not found", response.error?.details)
    }

    @Test
    fun `error with field errors should include field errors`() {
        val fieldErrors = listOf(
            ApiError.FieldError("email", "invalid", "Invalid email format"),
            ApiError.FieldError("age", -1, "Age must be positive"),
        )

        val response = ApiResponse.error<Any>(
            CommonErrorCode.VALIDATION_ERROR,
            "Validation failed",
            fieldErrors,
        )

        assertFalse(response.success)
        assertEquals(2, response.error?.fieldErrors?.size)
        assertEquals("email", response.error?.fieldErrors?.get(0)?.field)
    }
}
