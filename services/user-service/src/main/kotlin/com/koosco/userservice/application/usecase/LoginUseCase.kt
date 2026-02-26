package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.LoginCommand
import com.koosco.userservice.application.dto.AuthTokenDto
import com.koosco.userservice.application.port.LoginHistoryRepository
import com.koosco.userservice.application.port.RefreshTokenStorePort
import com.koosco.userservice.application.port.TokenGeneratorPort
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import com.koosco.userservice.domain.entity.LoginHistory
import com.koosco.userservice.domain.vo.Email
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val refreshTokenStorePort: RefreshTokenStorePort,
    private val loginHistoryRepository: LoginHistoryRepository,
) {
    fun execute(command: LoginCommand): AuthTokenDto {
        val member = userRepository.findByEmail(Email.of(command.email))

        if (member == null) {
            loginHistoryRepository.save(
                LoginHistory.failure(
                    userId = 0L,
                    ip = command.ip,
                    userAgent = command.userAgent,
                    reason = "USER_NOT_FOUND",
                ),
            )
            throw NotFoundException(MemberErrorCode.INVALID_CREDENTIALS)
        }

        if (member.passwordHash == null ||
            !passwordEncoder.matches(command.password, member.passwordHash!!.value)
        ) {
            loginHistoryRepository.save(
                LoginHistory.failure(
                    userId = member.id!!,
                    ip = command.ip,
                    userAgent = command.userAgent,
                    reason = "INVALID_PASSWORD",
                ),
            )
            throw NotFoundException(MemberErrorCode.INVALID_CREDENTIALS)
        }

        loginHistoryRepository.save(
            LoginHistory.success(
                userId = member.id!!,
                ip = command.ip,
                userAgent = command.userAgent,
            ),
        )

        val tokens = tokenGeneratorPort.generateTokens(
            userId = member.id!!,
            email = member.email.value,
            roles = listOf(member.role.name),
        )

        refreshTokenStorePort.save(member.id!!, tokens.refreshToken, tokens.refreshTokenExpiresIn)

        return tokens
    }
}
