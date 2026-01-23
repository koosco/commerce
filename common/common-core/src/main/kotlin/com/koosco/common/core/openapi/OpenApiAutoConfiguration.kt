package com.koosco.common.core.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * Auto-configuration for OpenAPI/Swagger documentation.
 *
 * This configuration is only activated when:
 * 1. springdoc-openapi is on the classpath (ConditionalOnClass)
 * 2. common.openapi.enabled=true (default)
 * 3. No custom OpenAPI bean is defined (ConditionalOnMissingBean)
 *
 * Services can override this by:
 * - Defining their own OpenAPI bean
 * - Setting common.openapi.enabled=false
 * - Customizing via common.openapi.* properties
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI::class)
@ConditionalOnProperty(
    prefix = "common.openapi",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableConfigurationProperties(OpenApiProperties::class)
class OpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OpenAPI::class)
    fun openApi(properties: OpenApiProperties): OpenAPI {
        val openApi = OpenAPI()
            .info(buildInfo(properties))

        // Add servers if configured
        if (properties.servers.isNotEmpty()) {
            openApi.servers(
                properties.servers.map { serverConfig ->
                    Server()
                        .url(serverConfig.url)
                        .description(serverConfig.description)
                },
            )
        }

        // Add JWT Bearer authentication if enabled
        if (properties.jwtAuthEnabled) {
            openApi
                .components(
                    Components()
                        .addSecuritySchemes(
                            BEARER_AUTH_SCHEME,
                            SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT authentication token"),
                        ),
                )
                .addSecurityItem(SecurityRequirement().addList(BEARER_AUTH_SCHEME))
        }

        return openApi
    }

    private fun buildInfo(properties: OpenApiProperties): Info {
        val info = Info()
            .title(properties.title)
            .version(properties.version)
            .description(properties.description)

        if (properties.contactName.isNotBlank() || properties.contactEmail.isNotBlank()) {
            info.contact(
                Contact()
                    .name(properties.contactName.ifBlank { null })
                    .email(properties.contactEmail.ifBlank { null }),
            )
        }

        return info
    }

    companion object {
        const val BEARER_AUTH_SCHEME = "bearerAuth"
    }
}
