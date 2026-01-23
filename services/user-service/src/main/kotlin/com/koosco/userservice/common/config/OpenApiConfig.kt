package com.koosco.userservice.common.config

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
                .title("User Service API")
                .version("v1.0.0")
                .description(
                    """
                    # User Service API Documentation

                    This service manages user profiles and account management for the e-commerce platform.

                    ## Key Features
                    - **User Registration**: Create new user accounts
                    - **Profile Management**: Update user information and preferences
                    - **User Information Retrieval**: Fetch user details and profiles
                    - **Address Management**: Manage user shipping and billing addresses
                    - **Account Settings**: Update account configuration and preferences

                    ## Authentication
                    - JWT Bearer token authentication for user operations
                    - Public access for registration endpoint
                    """.trimIndent(),
                ),
        )
        .servers(
            listOf(
                Server().url("http://localhost:8081").description("Local development server"),
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
