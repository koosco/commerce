package com.koosco.commonobservability

import com.koosco.commonobservability.actuator.ActuatorProperties
import com.koosco.commonobservability.logging.LoggingProperties
import com.koosco.commonobservability.metrics.MetricsProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * Main configuration properties for observability
 * Aggregates all observability-related configuration
 */
@ConfigurationProperties(prefix = "observability")
data class ObservabilityProperties(
    /**
     * Enable/disable all observability features
     */
    var enabled: Boolean = true,

    /**
     * Logging configuration
     */
    @NestedConfigurationProperty
    var logging: LoggingProperties = LoggingProperties(),

    /**
     * Metrics configuration
     */
    @NestedConfigurationProperty
    var metrics: MetricsProperties = MetricsProperties(),

    /**
     * Actuator configuration
     */
    @NestedConfigurationProperty
    var actuator: ActuatorProperties = ActuatorProperties(),
)
