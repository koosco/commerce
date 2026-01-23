package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.ForceUpdateCommand
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode
import com.koosco.userservice.domain.vo.Phone
import org.springframework.transaction.annotation.Transactional

@UseCase
class ForceUpdateUseCase(private val userRepository: UserRepository) {

    @Transactional
    fun execute(command: ForceUpdateCommand) {
        val user = userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(
                UserErrorCode.USER_NOT_FOUND,
                "User with id ${command.userId} not found",
            )

        user.update(
            name = command.name,
            phone = Phone.of(command.phone),
        )
    }
}
