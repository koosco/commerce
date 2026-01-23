package com.koosco.authservice.infra.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(var secret: String = "", var expiration: Long = 0, var refreshExpiration: Long = 0) {
    @PostConstruct
    fun init() {
        println("=== JWT Properties Loaded ===")
        println("Secret length: ${secret.length}")
        println("==============================")
    }
}
