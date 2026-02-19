package com.koosco.inventoryservice.infra.storage.primary

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

/**
 * fileName       : RedisScriptLoader
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:11
 * description    :
 */
@Component
class RedisScriptLoader(private val resourceLoader: ResourceLoader) {
    fun load(path: String): String = resourceLoader.getResource("classpath:$path")
        .inputStream
        .bufferedReader()
        .readText()
}
