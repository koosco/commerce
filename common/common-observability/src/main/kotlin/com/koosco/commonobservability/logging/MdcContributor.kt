package com.koosco.commonobservability.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.spi.ContextAwareBase
import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers
import org.slf4j.MDC

/**
 * MDC Contributor for Logstash encoder
 * Contributes MDC context to JSON log output
 */
class MdcContributor : ContextAwareBase() {

    companion object {
        private val MDC_KEYS = setOf(
            MdcFilter.TRACE_ID_KEY,
            MdcFilter.SPAN_ID_KEY,
            MdcFilter.REQUEST_URI_KEY,
            MdcFilter.REQUEST_METHOD_KEY,
            MdcFilter.CLIENT_IP_KEY,
            MdcFilter.USER_AGENT_KEY,
        )
    }

    fun contribute(event: ILoggingEvent): LogstashMarker? {
        val mdcProperties = event.mdcPropertyMap
        if (mdcProperties.isNullOrEmpty()) {
            return null
        }

        val markers = mutableListOf<LogstashMarker>()

        // Add all relevant MDC properties
        mdcProperties.forEach { (key, value) ->
            if (key in MDC_KEYS && !value.isNullOrEmpty()) {
                markers.add(Markers.append(key, value))
            }
        }

        return if (markers.isEmpty()) null else Markers.aggregate(markers)
    }

    /**
     * Manually add custom fields to MDC
     */
    fun addCustomField(key: String, value: String) {
        MDC.put(key, value)
    }

    /**
     * Remove custom field from MDC
     */
    fun removeCustomField(key: String) {
        MDC.remove(key)
    }
}
