package com.koosco.authservice.infra.config

import com.koosco.commonsecurity.config.PublicEndpointProvider
import org.springframework.stereotype.Component

@Component
class AuthPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/auth", // 회원가입
        "/api/auth/login", // 로그인
        "/actuator/health/**", // Kubernetes health checks
        "/actuator/info", // Application info
    )
}
