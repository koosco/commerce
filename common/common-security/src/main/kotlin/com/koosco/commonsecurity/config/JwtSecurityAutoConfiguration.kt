package com.koosco.commonsecurity.config

import com.koosco.commonsecurity.jwt.JwtAuthenticationFilter
import com.koosco.commonsecurity.jwt.JwtProperties
import com.koosco.commonsecurity.jwt.JwtTokenProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ConditionalOnProperty(prefix = "jwt", name = ["secret"])
@EnableConfigurationProperties(JwtProperties::class)
@ComponentScan(basePackages = ["com.koosco.commonsecurity"])
class JwtSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun jwtTokenProvider(jwtProperties: JwtProperties): JwtTokenProvider = JwtTokenProvider(jwtProperties)

    @Bean
    @ConditionalOnMissingBean
    fun jwtAuthenticationFilter(
        jwtTokenProvider: JwtTokenProvider,
        jwtProperties: JwtProperties,
    ): JwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenProvider, jwtProperties)
}
