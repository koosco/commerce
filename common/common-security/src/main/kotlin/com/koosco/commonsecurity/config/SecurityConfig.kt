package com.koosco.commonsecurity.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import com.koosco.commonsecurity.jwt.JwtAuthenticationEntryPoint
import com.koosco.commonsecurity.jwt.JwtAuthenticationFilter
import com.koosco.commonsecurity.jwt.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
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
        val methodBasedEndpoints = getMethodBasedPublicEndpoints()

        return http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(*publicPaths).permitAll()

                methodBasedEndpoints.forEach { (method, paths) ->
                    auth.requestMatchers(method, *paths).permitAll()
                }

                auth.anyRequest().authenticated()
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

    private fun getMethodBasedPublicEndpoints(): Map<HttpMethod, Array<String>> {
        val merged = mutableMapOf<HttpMethod, MutableList<String>>()
        publicEndpointProviders.forEach { provider ->
            provider.publicEndpointsByMethod().forEach { (method, paths) ->
                merged.getOrPut(method) { mutableListOf() }.addAll(paths)
            }
        }
        return merged.mapValues { it.value.toTypedArray() }
    }
}
