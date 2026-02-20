package com.koosco.userservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import com.koosco.userservice.api.LoginRequest
import com.koosco.userservice.application.usecase.LoginUseCase
import com.koosco.userservice.application.usecase.LogoutUseCase
import com.koosco.userservice.application.usecase.RefreshTokenUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "Authentication operations")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) {

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 토큰을 발급합니다.")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): ApiResponse<Any> {
        val tokens = loginUseCase.execute(request.toCommand())

        response.addHeader("Authorization", tokens.accessToken)

        putRefreshTokenInCookie(response, tokens.refreshToken, tokens.refreshTokenExpiresIn)

        return ApiResponse.success()
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 토큰을 발급합니다.")
    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest, response: HttpServletResponse): ApiResponse<Any> {
        val refreshToken = extractRefreshToken(request)

        val tokens = refreshTokenUseCase.execute(refreshToken)

        response.addHeader("Authorization", tokens.accessToken)

        putRefreshTokenInCookie(response, tokens.refreshToken, tokens.refreshTokenExpiresIn)

        return ApiResponse.success()
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제합니다.")
    @PostMapping("/logout")
    fun logout(@AuthId userId: Long, response: HttpServletResponse): ApiResponse<Any> {
        logoutUseCase.execute(userId)

        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()
        response.addHeader("Set-Cookie", cookie.toString())

        return ApiResponse.success()
    }

    private fun extractRefreshToken(request: HttpServletRequest): String = request.cookies?.firstOrNull {
        it.name == "refreshToken"
    }?.value
        ?: throw com.koosco.common.core.exception.BaseException(
            com.koosco.userservice.common.MemberErrorCode.INVALID_REFRESH_TOKEN,
        )

    private fun putRefreshTokenInCookie(response: HttpServletResponse, refreshToken: String, expiresIn: Long) {
        val cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(expiresIn)
            .sameSite("Strict")
            .build()
        response.addHeader("Set-Cookie", cookie.toString())
    }
}
