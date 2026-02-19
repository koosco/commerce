package com.koosco.userservice.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtGenerationProperties(val secret: String = "", val expiration: Long = 0, val refreshExpiration: Long = 0)
