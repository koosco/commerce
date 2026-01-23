package com.koosco.paymentservice.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    /**
     * timeout, interceptor, header 설정 가능
     */
    @Bean
    fun portOneRestClient(): RestClient = RestClient.create()
}
