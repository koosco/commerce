package com.koosco.common.core.openapi

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for OpenAPI/Swagger documentation.
 *
 * Each service can customize these properties in their application.yaml:
 * ```yaml
 * common:
 *   openapi:
 *     enabled: true
 *     title: "My Service API"
 *     version: "v1.0.0"
 *     description: "API documentation for my service"
 *     servers:
 *       - url: http://localhost:8080
 *         description: Local development
 * ```
 */
@ConfigurationProperties(prefix = "common.openapi")
data class OpenApiProperties(
    /**
     * Enable or disable OpenAPI auto-configuration.
     * Default: true
     */
    val enabled: Boolean = true,

    /**
     * API title displayed in Swagger UI.
     * Default: "API Documentation"
     */
    val title: String = "API Documentation",

    /**
     * API version.
     * Default: "v1.0.0"
     */
    val version: String = "v1.0.0",

    /**
     * API description. Supports markdown formatting.
     */
    val description: String = "",

    /**
     * Contact name for the API.
     */
    val contactName: String = "",

    /**
     * Contact email for the API.
     */
    val contactEmail: String = "",

    /**
     * List of server configurations.
     */
    val servers: List<ServerConfig> = emptyList(),

    /**
     * Enable JWT Bearer authentication scheme.
     * Default: true
     */
    val jwtAuthEnabled: Boolean = true,
) {
    /**
     * Server configuration for OpenAPI.
     */
    data class ServerConfig(
        /**
         * Server URL (e.g., "http://localhost:8080")
         */
        val url: String,

        /**
         * Server description (e.g., "Local development server")
         */
        val description: String = "",
    )
}
