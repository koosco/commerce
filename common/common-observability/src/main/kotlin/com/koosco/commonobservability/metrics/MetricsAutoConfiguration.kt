package com.koosco.commonobservability.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

/**
 * Auto-configuration for metrics
 * Automatically configures Prometheus metrics and common tags
 */
@AutoConfiguration(after = [MetricsAutoConfiguration::class])
@ConditionalOnClass(MeterRegistry::class)
@ConditionalOnProperty(
    prefix = "observability.metrics",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableConfigurationProperties(MetricsProperties::class)
@Import(PrometheusConfig::class)
class MetricsAutoConfiguration {

    /**
     * Common metric tags provider
     */
    @Bean
    fun commonMetricTags(
        metricsProperties: MetricsProperties,
        @Value("\${spring.application.name:unknown}") applicationName: String,
    ): CommonMetricTags = CommonMetricTags(metricsProperties, applicationName)
}
