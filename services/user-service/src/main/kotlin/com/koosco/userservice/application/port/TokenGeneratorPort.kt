package com.koosco.userservice.application.port

import com.koosco.userservice.application.dto.AuthTokenDto

interface TokenGeneratorPort {
    fun generateTokens(userId: Long, email: String, roles: List<String>): AuthTokenDto

    fun validateRefreshToken(token: String): Boolean

    fun extractUserId(token: String): Long
}
