package com.koosco.commonsecurity.config

import org.springframework.http.HttpMethod

interface PublicEndpointProvider {
    fun publicEndpoints(): Array<String> = emptyArray()

    fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = emptyMap()
}
