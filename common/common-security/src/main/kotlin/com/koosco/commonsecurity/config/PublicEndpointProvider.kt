package com.koosco.commonsecurity.config

interface PublicEndpointProvider {
    fun publicEndpoints(): Array<String>
}
