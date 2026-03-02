package com.koosco.paymentservice.common

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class PaymentPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/payments/confirm",
        "/api/payments/*/cancel",
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
