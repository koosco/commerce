package com.koosco.userservice.infra.redis

import com.koosco.userservice.application.port.RefreshTokenStorePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisRefreshTokenAdapter(private val redisTemplate: RedisTemplate<String, String>) : RefreshTokenStorePort {

    override fun save(userId: Long, refreshToken: String, ttlSeconds: Long) {
        redisTemplate.opsForValue().set(
            key(userId),
            refreshToken,
            ttlSeconds,
            TimeUnit.SECONDS,
        )
    }

    override fun findByUserId(userId: Long): String? = redisTemplate.opsForValue().get(key(userId))

    override fun delete(userId: Long) {
        redisTemplate.delete(key(userId))
    }

    private fun key(userId: Long): String = "member:refresh-token:$userId"
}
