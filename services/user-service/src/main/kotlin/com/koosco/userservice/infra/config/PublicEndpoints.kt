package com.koosco.userservice.infra.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class PublicEndpoints : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/users",
        "/api/users/login",
        "/api/users/**",
    )
}
