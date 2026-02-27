package com.koosco.catalogservice.common.config

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

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()

        cacheManager.registerCustomCache(
            "productDetail",
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build(),
        )

        cacheManager.registerCustomCache(
            "promotionPrice",
            Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .recordStats()
                .build(),
        )

        cacheManager.registerCustomCache(
            "categoryTree",
            Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build(),
        )

        return cacheManager
    }
}
