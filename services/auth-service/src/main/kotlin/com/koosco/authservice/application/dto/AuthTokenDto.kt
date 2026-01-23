package com.koosco.authservice.application.dto

data class AuthTokenDto(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long, // 초 단위 (예: 604800 = 7일)
)
