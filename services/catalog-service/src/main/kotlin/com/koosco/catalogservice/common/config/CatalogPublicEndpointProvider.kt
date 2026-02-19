package com.koosco.catalogservice.common.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class CatalogPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = mapOf(
        HttpMethod.GET to arrayOf(
            "/api/products",
            "/api/products/{id}",
            "/api/products/{id}/skus",
            "/api/categories",
            "/api/categories/tree",
        ),
    )

    override fun publicEndpoints(): Array<String> = arrayOf(
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
