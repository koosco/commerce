package com.koosco.commonobservability.logging

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered

/**
 * Auto-configuration for logging components
 * Automatically configures MDC filter and logging when web application is present
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "observability.logging",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@EnableConfigurationProperties(LoggingProperties::class)
class LoggingAutoConfiguration {

    /**
     * Register MDC Filter for web applications
     * Only active when web application context is present
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(
        prefix = "observability.logging",
        name = ["mdc-enabled"],
        havingValue = "true",
        matchIfMissing = true,
    )
    @ConditionalOnClass(name = ["jakarta.servlet.Filter"])
    fun mdcFilterRegistration(): FilterRegistrationBean<MdcFilter> {
        val registration = FilterRegistrationBean<MdcFilter>()
        registration.filter = MdcFilter()
        registration.order = Ordered.HIGHEST_PRECEDENCE
        registration.addUrlPatterns("/*")
        return registration
    }

    /**
     * MDC Contributor bean for custom MDC operations
     */
    @Bean
    @ConditionalOnProperty(
        prefix = "observability.logging",
        name = ["mdc-enabled"],
        havingValue = "true",
        matchIfMissing = true,
    )
    fun mdcContributor(): MdcContributor = MdcContributor()
}
