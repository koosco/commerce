package com.koosco.orderservice.common.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class OrderPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
