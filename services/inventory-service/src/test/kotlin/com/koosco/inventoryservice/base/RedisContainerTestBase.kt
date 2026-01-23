package com.koosco.inventoryservice.base

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * fileName       : RedisContainerTestBase
 * author         : koo
 * date           : 2025. 12. 30. 오전 12:28
 * description    : Redis Testcontainers를 사용하는 테스트의 베이스 클래스
 *                  - Redis 컨테이너를 자동으로 시작/종료
 *                  - Spring Boot의 Redis 설정을 동적으로 구성
 *                  - 모든 Redis 관련 통합 테스트에서 상속하여 사용
 */
@Testcontainers
abstract class RedisContainerTestBase {

    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7.2-alpine")
            .withExposedPorts(6379)
            .withReuse(true)

        @DynamicPropertySource
        @JvmStatic
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379).toString() }
        }
    }
}
