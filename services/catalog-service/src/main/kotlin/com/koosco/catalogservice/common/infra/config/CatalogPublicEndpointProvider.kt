package com.koosco.catalogservice.common.infra.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class CatalogPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/products/**", // Public product browsing
        "/api/categories/**", // Public category browsing
        "/actuator/health/**", // Kubernetes health checks
        "/actuator/info", // Application info
        "/swagger-ui/**", // API documentation
        "/v3/api-docs/**", // OpenAPI specs
    )
}
