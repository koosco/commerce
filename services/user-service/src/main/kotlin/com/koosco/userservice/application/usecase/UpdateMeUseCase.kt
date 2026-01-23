package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.UpdateUserCommand
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode
import com.koosco.userservice.domain.vo.Phone
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateMeUseCase(private val userRepository: UserRepository) {

    @Transactional
    fun execute(command: UpdateUserCommand) {
        val me = (
            userRepository.findActiveUserById(command.userId)
                ?: throw NotFoundException(
                    UserErrorCode.USER_NOT_FOUND,
                    "${command.userId}에 해당하는 사용자를 찾을 수 없습니다.",
                )
            )

        me.update(command.name, Phone.of(command.phone))
    }
}
