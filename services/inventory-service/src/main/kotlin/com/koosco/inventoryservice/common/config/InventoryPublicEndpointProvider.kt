package com.koosco.inventoryservice.common.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class InventoryPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = mapOf(
        HttpMethod.GET to arrayOf("/api/inventories/{skuId}"),
    )

    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/inventories/bulk",
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
