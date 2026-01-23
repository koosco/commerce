package com.koosco.inventoryservice.common.config

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
                .title("Inventory Service API")
                .version("v1.0.0")
                .description(
                    """
                    # Inventory Service API Documentation

                    Stock management service with Redis-based atomic operations.

                    ## Key Features
                    - **Atomic Stock Operations**: Redis Lua scripts for race-condition-free operations
                    - **Stock Reservation**: Reserve stock for pending orders
                    - **Stock Confirmation**: Permanently deduct reserved stock
                    - **Stock Cancellation**: Release reserved stock back to available inventory
                    - **Stock Refund**: Return confirmed stock back to available inventory
                    - **High-Concurrency Support**: Thread-safe operations for concurrent order processing

                    ## Authentication
                    - JWT Bearer token authentication for admin operations
                    - Anonymous access allowed for stock inquiry endpoints
                    """.trimIndent(),
                ),
        )
        .servers(
            listOf(
                Server().url("http://localhost:8083").description("Local development server"),
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
