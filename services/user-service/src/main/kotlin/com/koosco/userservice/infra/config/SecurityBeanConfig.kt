package com.koosco.userservice.infra.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtGenerationProperties::class)
class SecurityBeanConfig {

    @Bean
    fun passwordEncoder(): DelegatingPasswordEncoder = DelegatingPasswordEncoder(
        "bcrypt",
        mapOf("bcrypt" to BCryptPasswordEncoder()),
    )
}
