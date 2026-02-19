package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.port.RefreshTokenStorePort
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteMeUseCase(
    private val userRepository: UserRepository,
    private val refreshTokenStorePort: RefreshTokenStorePort,
) {

    @Transactional
    fun execute(userId: Long) {
        val member = (
            userRepository.findActiveUserById(userId)
                ?: throw NotFoundException(
                    MemberErrorCode.MEMBER_NOT_FOUND,
                    "${userId}에 해당하는 사용자를 찾을 수 없습니다.",
                )
            )

        member.withdraw()
        refreshTokenStorePort.delete(userId)
    }
}
