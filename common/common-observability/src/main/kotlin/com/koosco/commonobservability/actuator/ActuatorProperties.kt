package com.koosco.commonobservability.actuator

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for actuator endpoints
 */
@ConfigurationProperties(prefix = "observability.actuator")
data class ActuatorProperties(
    /**
     * Enable/disable actuator configuration
     */
    var enabled: Boolean = true,

    /**
     * Expose all endpoints (use with caution in production)
     */
    var exposeAll: Boolean = false,

    /**
     * Specific endpoints to expose
     */
    var exposedEndpoints: Set<String> = setOf(
        "health",
        "info",
        "metrics",
        "prometheus",
    ),

    /**
     * Enable detailed health information
     */
    var showDetails: String = "when-authorized",

    /**
     * Enable/disable health probes for k8s
     */
    var probesEnabled: Boolean = true,
)
