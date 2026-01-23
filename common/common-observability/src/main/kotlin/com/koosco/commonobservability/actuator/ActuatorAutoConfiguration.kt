package com.koosco.commonobservability.actuator

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

/**
 * Auto-configuration for actuator endpoints
 * Automatically configures actuator endpoint exposure
 */
@AutoConfiguration(after = [WebEndpointAutoConfiguration::class])
@ConditionalOnClass(name = ["org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier"])
@ConditionalOnProperty(
    prefix = "observability.actuator",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableConfigurationProperties(ActuatorProperties::class)
@Import(ActuatorExposureConfig::class)
class ActuatorAutoConfiguration
