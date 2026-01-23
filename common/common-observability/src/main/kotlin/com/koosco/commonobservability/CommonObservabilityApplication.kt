package com.koosco.commonobservability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Common Observability Module for MSA
 *
 * This is a library module intended to be imported by other Spring Boot applications.
 * It provides auto-configuration for:
 * - JSON format logging with MDC
 * - Prometheus metrics collection
 * - Actuator endpoint configuration
 * - Distributed tracing support
 *
 * Usage: Simply add this module as a dependency to enable all features automatically.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class CommonObservabilityApplication

fun main(args: Array<String>) {
    runApplication<CommonObservabilityApplication>(*args)
}
