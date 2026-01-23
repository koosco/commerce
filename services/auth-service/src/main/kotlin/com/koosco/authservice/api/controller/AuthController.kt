package com.koosco.authservice.api.controller

import com.koosco.authservice.api.dto.request.CreateUserRequest
import com.koosco.authservice.api.dto.request.LoginRequest
import com.koosco.authservice.application.usecase.LoginUseCase
import com.koosco.authservice.application.usecase.RegisterUseCase
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "Auth", description = "Authentication and authorization APIs")
@RestController
@RequestMapping("/api/auth")
class AuthController(private val registerUseCase: RegisterUseCase, private val loginUseCase: LoginUseCase) {

    @Hidden
    @Operation(
        summary = "Register user authentication credentials",
        description = """
            **Internal endpoint for user-service only.**

            Registers authentication credentials for a new user. This endpoint is called by user-service
            after successful user creation. Supports LOCAL and KAKAO providers.

            ### Request Body
            - `userId`: User ID from user-service
            - `email`: User email address
            - `provider`: Authentication provider (LOCAL or KAKAO)
            - `password`: Raw password (will be BCrypt encrypted)
            - `role`: User role (ROLE_USER or ROLE_ADMIN)
        """,
    )
    @SwaggerApiResponse(responseCode = "200", description = "Authentication credentials registered successfully")
    @SwaggerApiResponse(responseCode = "400", description = "Invalid request parameters")
    @SwaggerApiResponse(responseCode = "409", description = "Email already exists for the provider")
    @SwaggerApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    fun registerUser(
        @Parameter(description = "User authentication registration request", required = true)
        @RequestBody request: CreateUserRequest,
    ): ApiResponse<Any> {
        registerUseCase.execute(request.toCommand())

        return ApiResponse.success()
    }

    @Operation(
        summary = "로컬 사용자 로그인",
        description = "로컬 사용자로 로그인합니다.",
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ApiResponse<Any> {
        val dto = loginUseCase.execute(request.toCommand())

        response.addHeader("Authorization", dto.accessToken)

        val refreshTokenCookie = ResponseCookie.from("refreshToken", dto.refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(dto.refreshTokenExpiresIn)
            .sameSite("Strict")
            .build()
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        return ApiResponse.success()
    }
}
