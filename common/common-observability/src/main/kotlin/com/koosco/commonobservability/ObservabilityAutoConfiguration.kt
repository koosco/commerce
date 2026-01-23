package com.koosco.commonobservability

import com.koosco.commonobservability.actuator.ActuatorAutoConfiguration
import com.koosco.commonobservability.logging.LoggingAutoConfiguration
import com.koosco.commonobservability.metrics.MetricsAutoConfiguration
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

/**
 * Main auto-configuration for observability module
 * This configuration is automatically activated when the module is included
 * All features can be disabled via application properties
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "observability",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableConfigurationProperties(ObservabilityProperties::class)
@Import(
    LoggingAutoConfiguration::class,
    MetricsAutoConfiguration::class,
    ActuatorAutoConfiguration::class,
)
class ObservabilityAutoConfiguration(
    private val properties: ObservabilityProperties,
) {

    private val logger = LoggerFactory.getLogger(ObservabilityAutoConfiguration::class.java)

    @PostConstruct
    fun init() {
        logger.info("=================================================")
        logger.info("Common Observability Module Initialized")
        logger.info("=================================================")
        logger.info("Logging enabled: {}", properties.logging.enabled)
        logger.info("  - MDC Filter: {}", properties.logging.mdcEnabled)
        logger.info("  - JSON Format: {}", properties.logging.jsonEnabled)
        logger.info("Metrics enabled: {}", properties.metrics.enabled)
        logger.info("  - Prometheus: {}", properties.metrics.prometheusEnabled)
        logger.info("  - Environment: {}", properties.metrics.environment)
        logger.info("Actuator enabled: {}", properties.actuator.enabled)
        logger.info("  - Exposed endpoints: {}", properties.actuator.exposedEndpoints)
        logger.info("  - Health probes: {}", properties.actuator.probesEnabled)
        logger.info("=================================================")
    }
}
