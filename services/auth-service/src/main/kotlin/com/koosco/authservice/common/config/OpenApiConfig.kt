package com.koosco.authservice.common.config

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
                .title("Auth Service API")
                .version("v1.0.0")
                .description(
                    """
                    # Auth Service API Documentation

                    JWT authentication and authorization service. Handles user login, token generation, and token validation.

                    ## Key Features
                    - **User Authentication**: JWT token-based authentication
                    - **Token Generation**: Access token and refresh token generation
                    - **Password Security**: BCrypt encryption for password storage
                    - **Multi-Provider Support**: LOCAL and KAKAO authentication providers

                    ## Authentication
                    - JWT Bearer token authentication for protected endpoints
                    - Anonymous access allowed for login and registration
                    """.trimIndent(),
                ),
        )
        .servers(
            listOf(
                Server().url("http://localhost:8089").description("Local development server"),
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
