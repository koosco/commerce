package com.koosco.common.core.test

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base class for Redis integration tests using Testcontainers.
 *
 * Provides:
 * - Shared Redis container (started once per test class)
 * - Dynamic property configuration for Spring Data Redis
 *
 * Usage:
 * ```kotlin
 * @SpringBootTest
 * @ActiveProfiles("test")
 * class MyRedisTest : RedisContainerTestBase() {
 *     @Test
 *     fun `should store value in Redis`() {
 *         // test with real Redis
 *     }
 * }
 * ```
 */
@Testcontainers
abstract class RedisContainerTestBase {

    companion object {
        private const val REDIS_IMAGE = "redis:7.2-alpine"

        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer(REDIS_IMAGE)
            .withExposedPorts(6379)
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }
}
