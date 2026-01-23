package com.koosco.common.core.response

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * ResponseBodyAdvice to automatically wrap controller responses in ApiResponse.
 *
 * This is optional - services can choose to use this for automatic wrapping
 * or manually return ApiResponse from controllers.
 *
 * To exclude specific controllers or methods, use @ApiResponseIgnore annotation.
 *
 * Note: Automatically excludes:
 * - Responses that are already ApiResponse
 * - Responses from Spring Boot actuator endpoints
 * - Responses from Swagger/OpenAPI endpoints
 */
@RestControllerAdvice
class ApiResponseAdvice : ResponseBodyAdvice<Any> {

    companion object {
        private val EXCLUDED_PACKAGES = setOf(
            "org.springframework.boot.actuate",
            "org.springdoc",
            "springfox",
        )
    }

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        // Skip if already ApiResponse
        if (returnType.parameterType == ApiResponse::class.java) {
            return false
        }

        // Skip if method or class has @ApiResponseIgnore
        if (returnType.hasMethodAnnotation(ApiResponseIgnore::class.java)) {
            return false
        }

        returnType.containingClass.getAnnotation(ApiResponseIgnore::class.java)?.let {
            return false
        }

        // Skip excluded packages (actuator, swagger, etc.)
        val className = returnType.containingClass.name
        if (EXCLUDED_PACKAGES.any { className.startsWith(it) }) {
            return false
        }

        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        // Already wrapped, return as-is
        if (body is ApiResponse<*>) {
            return body
        }

        // Wrap in ApiResponse
        return ApiResponse.success(body)
    }
}

/**
 * Annotation to exclude a controller or method from automatic ApiResponse wrapping.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiResponseIgnore
