package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.ForceDeleteCommand
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class ForceDeleteUseCase(private val userRepository: UserRepository) {

    @Transactional
    fun execute(command: ForceDeleteCommand) {
        val user = userRepository.findActiveUserById(command.userId) ?: throw NotFoundException(
            UserErrorCode.USER_NOT_FOUND,
            "${command.userId}에 해당하는 사용자를 찾을 수 없습니다.",
        )

        user.forceDelete()

        // TODO : 이메일 발송, Auth Service token 제거
    }
}
