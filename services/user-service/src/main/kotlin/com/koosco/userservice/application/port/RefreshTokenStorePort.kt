package com.koosco.userservice.application.port

interface RefreshTokenStorePort {
    fun save(userId: Long, refreshToken: String, ttlSeconds: Long)

    fun findByUserId(userId: Long): String?

    fun delete(userId: Long)
}
