package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteMeUseCase(private val userRepository: UserRepository) {

    @Transactional
    fun execute(userId: Long) {
        val me = (
            userRepository.findActiveUserById(userId)
                ?: throw NotFoundException(
                    UserErrorCode.USER_NOT_FOUND,
                    "${userId}에 해당하는 사용자를 찾을 수 없습니다.",
                )
            )

        me.quit()
    }
}
