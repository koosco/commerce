package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.GetUserDetailCommand
import com.koosco.userservice.application.dto.UserDto
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode

@UseCase
class GetUserDetailUseCase(private val userRepository: UserRepository) {
    fun execute(command: GetUserDetailCommand): UserDto {
        val member = userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND)

        return UserDto(
            id = member.id!!,
            email = member.email.value,
            name = member.name,
            phone = member.phone?.value,
        )
    }
}
