package com.koosco.inventoryservice.inventory.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * fileName       : RedisConfig
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:07
 * description    :
 */
@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory()

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().also {
            it.connectionFactory = connectionFactory

            it.keySerializer = StringRedisSerializer()
            it.valueSerializer = StringRedisSerializer()
            it.hashKeySerializer = StringRedisSerializer()
            it.hashValueSerializer = StringRedisSerializer()

            it.afterPropertiesSet()
        }
}
