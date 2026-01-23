package com.koosco.commonobservability.actuator

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Actuator endpoint exposure configuration
 * Configures which endpoints are exposed based on properties
 */
@Configuration
@EnableConfigurationProperties(ActuatorProperties::class)
class ActuatorExposureConfig(
    private val actuatorProperties: ActuatorProperties,
) {
    // Configuration is handled via application properties
    // and Spring Boot's default actuator auto-configuration
}
