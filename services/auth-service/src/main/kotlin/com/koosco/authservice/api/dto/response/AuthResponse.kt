package com.koosco.authservice.api.dto.response

data class AuthResponse(val accessToken: String) {
    companion object {
        fun of(accessToken: String) = AuthResponse(accessToken = accessToken)
    }
}
