package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.ForceUpdateCommand
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import com.koosco.userservice.domain.vo.Phone
import org.springframework.transaction.annotation.Transactional

@UseCase
class ForceUpdateUseCase(private val userRepository: UserRepository) {

    @Transactional
    fun execute(command: ForceUpdateCommand) {
        val member = userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(
                MemberErrorCode.MEMBER_NOT_FOUND,
                "User with id ${command.userId} not found",
            )

        member.update(
            name = command.name,
            phone = Phone.of(command.phone),
        )
    }
}
