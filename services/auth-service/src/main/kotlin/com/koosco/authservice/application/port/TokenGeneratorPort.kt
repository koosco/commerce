package com.koosco.authservice.application.port

import com.koosco.authservice.application.dto.AuthTokenDto

interface TokenGeneratorPort {
    fun generateTokens(userId: Long, email: String, roles: List<String>): AuthTokenDto

    fun validateRefreshToken(token: String): Boolean
}
