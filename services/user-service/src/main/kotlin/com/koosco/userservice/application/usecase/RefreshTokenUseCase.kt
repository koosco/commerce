package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.BaseException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.dto.AuthTokenDto
import com.koosco.userservice.application.port.RefreshTokenStorePort
import com.koosco.userservice.application.port.TokenGeneratorPort
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode

@UseCase
class RefreshTokenUseCase(
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val refreshTokenStorePort: RefreshTokenStorePort,
    private val userRepository: UserRepository,
) {
    fun execute(refreshToken: String): AuthTokenDto {
        if (!tokenGeneratorPort.validateRefreshToken(refreshToken)) {
            throw BaseException(MemberErrorCode.INVALID_REFRESH_TOKEN)
        }

        val userId = tokenGeneratorPort.extractUserId(refreshToken)

        val storedToken = refreshTokenStorePort.findByUserId(userId)
        if (storedToken == null || storedToken != refreshToken) {
            throw BaseException(MemberErrorCode.INVALID_REFRESH_TOKEN)
        }

        val member = userRepository.findActiveUserById(userId)
            ?: throw NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND)

        val tokens = tokenGeneratorPort.generateTokens(
            userId = member.id!!,
            email = member.email.value,
            roles = listOf(member.role.name),
        )

        refreshTokenStorePort.save(member.id!!, tokens.refreshToken, tokens.refreshTokenExpiresIn)

        return tokens
    }
}
