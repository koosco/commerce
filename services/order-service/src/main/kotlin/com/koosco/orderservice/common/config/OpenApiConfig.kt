package com.koosco.orderservice.common.config

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
                .title("Order Service API")
                .version("v1.0.0")
                .description(
                    """
                    # Order Service API Documentation

                    This service manages Products and Categories for the e-commerce platform.

                    ## Key Features
                    - **Product Management**: Create, read, update, delete products with options
                    - **Category Management**: Hierarchical category tree structure
                    - **Public Access**: GET endpoints are publicly accessible
                    - **Admin Operations**: POST/PUT/DELETE require ADMIN role

                    ## Authentication
                    - JWT Bearer token authentication for admin operations
                    - Anonymous access allowed for order browsing
                    """.trimIndent(),
                ),
        )
        .servers(
            listOf(
                Server().url("http://localhost:8080").description("Local development server"),
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
