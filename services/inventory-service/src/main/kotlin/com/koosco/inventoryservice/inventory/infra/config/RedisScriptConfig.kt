package com.koosco.inventoryservice.inventory.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.DefaultRedisScript
import java.nio.charset.StandardCharsets

/**
 * fileName       : RedisScriptConfig
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:12
 * description    :
 */
@Configuration
class RedisScriptConfig {

    @Bean
    fun addStockScript() = load("redis/script/add_stock.lua")

    @Bean
    fun reserveStockScript() = load("redis/script/reserve_stock.lua")

    @Bean
    fun confirmStockScript() = load("redis/script/confirm_stock.lua")

    @Bean
    fun cancelStockScript() = load("redis/script/cancel_stock.lua")

    @Bean
    fun decreaseStockScript() = load("redis/script/decrease_stock.lua")

    private fun load(path: String): DefaultRedisScript<Long> {
        val text = ClassPathResource(path)
            .inputStream
            .bufferedReader(StandardCharsets.UTF_8)
            .use { it.readText() }

        return DefaultRedisScript<Long>().apply {
            setScriptText(text)
            resultType = Long::class.java
        }
    }
}
