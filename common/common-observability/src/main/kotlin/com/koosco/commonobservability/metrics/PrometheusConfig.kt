package com.koosco.commonobservability.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Prometheus metrics configuration
 * Configures common metrics and tags for Prometheus monitoring
 */
@Configuration
class PrometheusConfig(
    private val commonMetricTags: CommonMetricTags,
) {

    /**
     * Configure common tags for all metrics
     */
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> = MeterRegistryCustomizer { registry ->
        registry.config().commonTags(commonMetricTags.getCommonTags())
    }

    /**
     * JVM Memory Metrics
     */
    @Bean
    fun jvmMemoryMetrics(): JvmMemoryMetrics = JvmMemoryMetrics()

    /**
     * JVM GC Metrics
     */
    @Bean
    fun jvmGcMetrics(): JvmGcMetrics = JvmGcMetrics()

    /**
     * JVM Thread Metrics
     */
    @Bean
    fun jvmThreadMetrics(): JvmThreadMetrics = JvmThreadMetrics()

    /**
     * ClassLoader Metrics
     */
    @Bean
    fun classLoaderMetrics(): ClassLoaderMetrics = ClassLoaderMetrics()

    /**
     * Processor Metrics
     */
    @Bean
    fun processorMetrics(): ProcessorMetrics = ProcessorMetrics()

    /**
     * Uptime Metrics
     */
    @Bean
    fun uptimeMetrics(): UptimeMetrics = UptimeMetrics()
}
