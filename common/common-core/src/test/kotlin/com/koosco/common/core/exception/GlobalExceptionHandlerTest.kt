package com.koosco.common.core.exception

import com.koosco.common.core.response.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.bind.annotation.*

@WebMvcTest(TestController::class)
@ContextConfiguration(classes = [GlobalExceptionHandlerTest.TestConfig::class, TestController::class, GlobalExceptionHandler::class])
class GlobalExceptionHandlerTest {

    @Configuration
    @EnableAutoConfiguration
    class TestConfig

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should handle BaseException`() {
        mockMvc.perform(get("/test/not-found"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-404"))
    }

    @Test
    fun `should handle ValidationException with field errors`() {
        mockMvc.perform(get("/test/validation"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-400-004"))
            .andExpect(jsonPath("$.error.fieldErrors").isArray)
    }

    @Test
    fun `should handle MethodArgumentNotValidException`() {
        val invalidJson = """{"email":"invalid","age":-1}"""

        mockMvc.perform(
            post("/test/validate-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-400-004"))
            .andExpect(jsonPath("$.error.fieldErrors").isArray)
    }

    @Test
    fun `should handle MissingServletRequestParameterException`() {
        mockMvc.perform(get("/test/required-param"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-400-003"))
    }

    @Test
    fun `should handle MethodArgumentTypeMismatchException`() {
        mockMvc.perform(get("/test/type-mismatch").param("id", "not-a-number"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-400-002"))
    }

    @Test
    fun `should handle HttpMessageNotReadableException`() {
        mockMvc.perform(
            post("/test/validate-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json{"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-400-001"))
    }

    @Test
    fun `should handle HttpRequestMethodNotSupportedException`() {
        mockMvc.perform(delete("/test/not-found"))
            .andExpect(status().isMethodNotAllowed)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-405"))
    }

    @Test
    fun `should handle generic Exception`() {
        mockMvc.perform(get("/test/unexpected"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("COMMON-500"))
    }

    @Test
    fun `should return success response for normal endpoint`() {
        mockMvc.perform(get("/test/success"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.message").value("Hello"))
    }
}

/**
 * Test controller for exception handler testing.
 */
@RestController
@RequestMapping("/test")
class TestController {

    data class TestRequest(
        @field:NotBlank(message = "Name is required")
        val name: String?,

        @field:Email(message = "Invalid email format")
        val email: String?,

        @field:Min(value = 0, message = "Age must be positive")
        val age: Int?,
    )

    @GetMapping("/success")
    fun success(): ApiResponse<Map<String, String>> = ApiResponse.success(mapOf("message" to "Hello"))

    @GetMapping("/not-found")
    fun notFound(): ApiResponse<Nothing> = throw NotFoundException(message = "Resource not found")

    @GetMapping("/validation")
    fun validation(): ApiResponse<Nothing> = throw ValidationException(
        message = "Validation failed",
        fieldErrors = listOf(
            com.koosco.common.core.error.ApiError.FieldError("field", "value", "Invalid"),
        ),
    )

    @PostMapping("/validate-body")
    fun validateBody(@Valid @RequestBody request: TestRequest): ApiResponse<TestRequest> = ApiResponse.success(request)

    @GetMapping("/required-param")
    fun requiredParam(@RequestParam name: String): ApiResponse<String> = ApiResponse.success(name)

    @GetMapping("/type-mismatch")
    fun typeMismatch(@RequestParam id: Int): ApiResponse<Int> = ApiResponse.success(id)

    @GetMapping("/unexpected")
    fun unexpected(): ApiResponse<Nothing> = throw RuntimeException("Unexpected error")
}
