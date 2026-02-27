package com.koosco.catalogservice.infra.redis

import com.koosco.catalogservice.application.command.SearchClickCommand
import com.koosco.catalogservice.application.port.SearchClickLogPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Redis 기반 검색 클릭 로그 저장소.
 * Sorted Set을 사용하여 검색 쿼리별 클릭 위치를 저장한다.
 * - key: catalog:search:click:{query}
 * - member: {productId}:{position}
 * - score: position (정렬용)
 * TTL을 설정하여 오래된 데이터를 자동으로 정리한다.
 */
@Component
class RedisSearchClickLogAdapter(private val redisTemplate: StringRedisTemplate) : SearchClickLogPort {

    override fun save(command: SearchClickCommand) {
        val key = clickKey(command.searchQuery)
        val member = "${command.clickedProductId}:${command.clickPosition}"
        val score = command.clickPosition.toDouble()

        redisTemplate.opsForZSet().add(key, member, score)
        redisTemplate.expire(key, TTL)
    }

    override fun getClickPositions(searchQuery: String): List<Int> {
        val key = clickKey(searchQuery)
        val members = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1) ?: return emptyList()

        return members
            .mapNotNull { it.score?.toInt() }
            .sorted()
    }

    override fun getAllSearchQueries(): Set<String> {
        val pattern = "$KEY_PREFIX*"
        val keys = redisTemplate.keys(pattern)
        return keys.map { it.removePrefix(KEY_PREFIX) }.toSet()
    }

    companion object {
        private const val KEY_PREFIX = "catalog:search:click:"
        private val TTL = Duration.ofDays(7)

        fun clickKey(query: String): String = "$KEY_PREFIX$query"
    }
}
