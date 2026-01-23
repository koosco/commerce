package com.koosco.authservice.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder

@Configuration
class AuthSecurityConfig {

    @Bean
    fun passwordEncoder(): DelegatingPasswordEncoder = DelegatingPasswordEncoder(
        "bcrypt",
        mapOf("bcrypt" to BCryptPasswordEncoder()),
    )
}
