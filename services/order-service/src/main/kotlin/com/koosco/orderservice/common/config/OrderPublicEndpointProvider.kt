package com.koosco.orderservice.common.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class OrderPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/**",
//        "/api/orders/**", // Public product browsing
//        "/api/carts/**", // Public category browsing
//        "/api/coupons/**", // Public category browsing
//        "/actuator/health/**", // Kubernetes health checks
//        "/actuator/info", // Application info
//        "/swagger-ui/**", // API documentation
//        "/v3/api-docs/**", // OpenAPI specs
    )
}
