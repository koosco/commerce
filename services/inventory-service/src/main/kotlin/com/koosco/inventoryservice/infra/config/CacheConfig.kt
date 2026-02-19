package com.koosco.inventoryservice.infra.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    companion object {
        const val STOCK_QUERY_CACHE = "stockQuery"
    }

    @Bean
    fun cacheManager(): CacheManager = CaffeineCacheManager(STOCK_QUERY_CACHE).apply {
        setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .maximumSize(10_000)
                .recordStats(),
        )
    }
}
