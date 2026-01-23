package com.koosco.commonobservability.metrics

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for metrics
 */
@ConfigurationProperties(prefix = "observability.metrics")
data class MetricsProperties(
    /**
     * Enable/disable metrics configuration
     */
    var enabled: Boolean = true,

    /**
     * Enable/disable Prometheus metrics
     */
    var prometheusEnabled: Boolean = true,

    /**
     * Common tags to apply to all metrics
     */
    var commonTags: Map<String, String> = emptyMap(),

    /**
     * Application name tag
     */
    var applicationName: String? = null,

    /**
     * Environment tag (dev, staging, production)
     */
    var environment: String = "development",

    /**
     * Additional custom tags
     */
    var customTags: Map<String, String> = emptyMap(),
)
