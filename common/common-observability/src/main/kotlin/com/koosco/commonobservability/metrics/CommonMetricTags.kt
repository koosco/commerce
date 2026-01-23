package com.koosco.commonobservability.metrics

import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import org.springframework.beans.factory.annotation.Value
import java.net.InetAddress

/**
 * Common metric tags provider
 * Provides consistent tags across all metrics in the MSA environment
 */
class CommonMetricTags(
    private val metricsProperties: MetricsProperties,
    @Value("\${spring.application.name:unknown}") private val applicationName: String,
) {

    companion object {
        const val TAG_APPLICATION = "application"
        const val TAG_ENVIRONMENT = "environment"
        const val TAG_INSTANCE = "instance"
        const val TAG_HOST = "host"
        const val TAG_VERSION = "version"
    }

    /**
     * Get common tags for all metrics
     */
    fun getCommonTags(): Tags {
        val tags = mutableListOf<Tag>()

        // Application name
        val appName = metricsProperties.applicationName ?: applicationName
        tags.add(Tag.of(TAG_APPLICATION, appName))

        // Environment
        tags.add(Tag.of(TAG_ENVIRONMENT, metricsProperties.environment))

        // Host information (useful in k8s environments)
        try {
            val hostname = InetAddress.getLocalHost().hostName
            tags.add(Tag.of(TAG_HOST, hostname))
        } catch (e: Exception) {
            tags.add(Tag.of(TAG_HOST, "unknown"))
        }

        // Instance ID (can be overridden by k8s pod name)
        val instanceId = System.getenv("HOSTNAME")
            ?: System.getenv("POD_NAME")
            ?: "unknown"
        tags.add(Tag.of(TAG_INSTANCE, instanceId))

        // Add common tags from properties
        metricsProperties.commonTags.forEach { (key, value) ->
            tags.add(Tag.of(key, value))
        }

        // Add custom tags from properties
        metricsProperties.customTags.forEach { (key, value) ->
            tags.add(Tag.of(key, value))
        }

        return Tags.of(tags)
    }

    /**
     * Create custom tag
     */
    fun createTag(key: String, value: String): Tag = Tag.of(key, value)

    /**
     * Create multiple custom tags
     */
    fun createTags(vararg pairs: Pair<String, String>): Tags = Tags.of(pairs.map { (key, value) -> Tag.of(key, value) })
}
