package com.koosco.paymentservice.common

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

/**
 * fileName       : PaymentPublicEndpointProvider
 * author         : koo
 * date           : 2025. 12. 24. 오후 7:44
 * description    :
 */
@Component
class PaymentPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/test/**", // Public product browsing
        "/api/carts/**", // Public category browsing
        "/api/coupons/**", // Public category browsing
        "/actuator/health/**", // Kubernetes health checks
        "/actuator/info", // Application info
        "/swagger-ui/**", // API documentation
        "/v3/api-docs/**", // OpenAPI specs
    )
}
