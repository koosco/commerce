package com.koosco.commonsecurity.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import com.koosco.commonsecurity.jwt.JwtAuthenticationEntryPoint
import com.koosco.commonsecurity.jwt.JwtAuthenticationFilter
import com.koosco.commonsecurity.jwt.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.collections.flatMap

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val publicEndpointProviders: List<PublicEndpointProvider>,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val publicPaths = getPublicEndpoints() + getServiceSpecificPublicEndpoints()

        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*publicPaths).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .build()
    }

    private fun getPublicEndpoints(): Array<String> = arrayOf(
        "/actuator/**",
        "/health",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )

    private fun getServiceSpecificPublicEndpoints(): Array<String> = publicEndpointProviders
        .flatMap { it.publicEndpoints().asIterable() }
        .toTypedArray()
}
