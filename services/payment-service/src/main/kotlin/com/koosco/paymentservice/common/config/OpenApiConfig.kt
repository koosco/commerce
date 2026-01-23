package com.koosco.paymentservice.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.text.trimIndent

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Payment Service API")
                .version("v1.0.0")
                .description(
                    """
                    # Payment Service API Documentation

                    This service manages payment processing for order transactions in the e-commerce platform.

                    ## Key Features
                    - **Payment Initialization**: Create payment records for orders
                    - **Payment Processing**: Process payments through external gateways
                    - **Payment Status Tracking**: Track payment status changes and updates
                    - **Refund Management**: Handle full and partial refunds
                    - **Gateway Integration**: Integration with external payment gateways

                    ## Authentication
                    - JWT Bearer token authentication for payment operations
                    - Anonymous access allowed for payment status checking
                    """.trimIndent(),
                ),
        )
        .servers(
            listOf(
                Server().url("http://localhost:8087").description("Local development server"),
                Server().url("https://api.koosco.com").description("Production server"),
            ),
        )
        .components(
            Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT authentication token"),
                ),
        )
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
}
