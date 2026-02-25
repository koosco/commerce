package com.koosco.paymentservice.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@ConfigurationProperties(prefix = "payment.client")
data class PaymentClientProperties(
    val connectTimeout: Duration = Duration.ofSeconds(5),
    val readTimeout: Duration = Duration.ofSeconds(5),
)

@Configuration
class RestClientConfig {

    @Bean
    fun portOneRestClient(properties: PaymentClientProperties): RestClient {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(properties.connectTimeout)
            setReadTimeout(properties.readTimeout)
        }
        return RestClient.builder()
            .requestFactory(factory)
            .build()
    }
}
