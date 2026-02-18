package com.koosco.inventoryservice.base

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull

/**
 * RedisContainerTestBase 사용 예제 및 검증 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class RedisContainerTestBaseTest : RedisContainerTestBase() {

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Test
    fun `Redis 컨테이너가 실행 중이어야 한다`() {
        assertTrue(redis.isRunning, "Redis container should be running")
    }

    @Test
    fun `Redis 연결이 정상적으로 설정되어야 한다`() {
        assertNotNull(redisTemplate, "RedisTemplate should be autowired")
    }

    @Test
    fun `Redis에 데이터를 저장하고 조회할 수 있어야 한다`() {
        // given
        val key = "test:key"
        val value = "test-value"

        // when
        redisTemplate.opsForValue().set(key, value)
        val result = redisTemplate.opsForValue().get(key)

        // then
        assertEquals(value, result, "Retrieved value should match the stored value")
    }

    @Test
    fun `Redis에서 키를 삭제할 수 있어야 한다`() {
        // given
        val key = "test:delete"
        redisTemplate.opsForValue().set(key, "to-be-deleted")

        // when
        val deleted = redisTemplate.delete(key)

        // then
        assertTrue(deleted, "Key should be deleted successfully")
    }
}
