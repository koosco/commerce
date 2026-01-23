package com.koosco.commonobservability.logging

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for logging
 */
@ConfigurationProperties(prefix = "observability.logging")
data class LoggingProperties(
    /**
     * Enable/disable logging configuration
     */
    var enabled: Boolean = true,

    /**
     * Enable/disable MDC filter
     */
    var mdcEnabled: Boolean = true,

    /**
     * Enable/disable JSON format logging
     */
    var jsonEnabled: Boolean = true,

    /**
     * Log level for the application
     */
    var level: String = "INFO",

    /**
     * Additional MDC keys to include in logs
     */
    var additionalMdcKeys: Set<String> = emptySet(),
)
