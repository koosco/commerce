package com.koosco.orderservice.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@ConfigurationProperties(prefix = "order.catalog")
data class CatalogClientProperties(
    val baseUrl: String = "http://localhost:8084",
    val connectTimeout: Duration = Duration.ofSeconds(2),
    val readTimeout: Duration = Duration.ofSeconds(2),
)

@Configuration
@EnableConfigurationProperties(CatalogClientProperties::class)
class CatalogRestClientConfig {

    @Bean
    fun catalogRestClient(properties: CatalogClientProperties): RestClient {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(properties.connectTimeout)
            setReadTimeout(properties.readTimeout)
        }

        return RestClient.builder()
            .baseUrl(properties.baseUrl)
            .requestFactory(factory)
            .build()
    }
}
