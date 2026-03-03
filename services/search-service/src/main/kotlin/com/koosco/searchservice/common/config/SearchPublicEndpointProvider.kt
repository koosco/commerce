package com.koosco.searchservice.common.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class SearchPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = mapOf(
        HttpMethod.GET to arrayOf(
            "/api/search/products",
        ),
    )

    override fun publicEndpoints(): Array<String> = arrayOf(
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
