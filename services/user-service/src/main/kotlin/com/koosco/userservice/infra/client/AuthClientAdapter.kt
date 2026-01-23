package com.koosco.userservice.infra.client

import com.koosco.common.core.error.CommonErrorCode
import com.koosco.common.core.exception.ExternalServiceException
import com.koosco.userservice.application.port.AuthServiceClient
import com.koosco.userservice.domain.enums.AuthProvider
import com.koosco.userservice.domain.enums.UserRole
import com.koosco.userservice.infra.client.dto.CreateUserRequest
import feign.FeignException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthClientAdapter(
    private val authClient: AuthClient,
    @Value("\${auth-service.url}")
    private val authServiceUrl: String,
) : AuthServiceClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun init() {
        logger.info("AuthClient initialized with URL: $authServiceUrl")
    }

    override fun notifyUserCreated(
        userId: Long,
        password: String,
        email: String,
        provider: AuthProvider?,
        role: UserRole,
    ) {
        try {
            authClient.createUser(
                CreateUserRequest(
                    userId = userId,
                    email = email,
                    password = password,
                    provider = provider,
                    role = role,
                ),
            )
        } catch (e: FeignException) {
            throw ExternalServiceException(
                CommonErrorCode.EXTERNAL_SERVICE_ERROR,
                "Auth service 호출 실패: ${e.message}",
            )
        }
    }
}
