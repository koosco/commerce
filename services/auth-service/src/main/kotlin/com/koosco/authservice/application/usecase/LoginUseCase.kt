package com.koosco.authservice.application.usecase

import com.koosco.authservice.application.dto.AuthTokenDto
import com.koosco.authservice.application.dto.LoginCommand
import com.koosco.authservice.application.port.AuthPersistPort
import com.koosco.authservice.application.port.TokenGeneratorPort
import com.koosco.authservice.common.AuthErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@UseCase
class LoginUseCase(
    private val authPersistPort: AuthPersistPort,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGeneratorPort: TokenGeneratorPort,
) {
    @Transactional
    fun execute(toDto: LoginCommand): AuthTokenDto {
        val userAuth = authPersistPort.findByEmail(toDto.email)
            ?: throw NotFoundException(AuthErrorCode.PROVIDER_USER_NOT_FOUND)

        if (!passwordEncoder.matches(toDto.password, userAuth.password.value)) {
            throw NotFoundException(AuthErrorCode.PROVIDER_USER_NOT_FOUND)
        }

        val tokens = tokenGeneratorPort.generateTokens(
            userId = userAuth.userId,
            email = userAuth.email.value,
            roles = listOf(userAuth.role.name),
        )

        userAuth.storeRefreshToken(tokens.refreshToken)

        return tokens
    }
}
