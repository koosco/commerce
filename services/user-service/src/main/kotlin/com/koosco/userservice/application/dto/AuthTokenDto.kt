package com.koosco.userservice.application.dto

data class AuthTokenDto(val accessToken: String, val refreshToken: String, val refreshTokenExpiresIn: Long)
