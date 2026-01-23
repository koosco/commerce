package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.GetUserDetailCommand
import com.koosco.userservice.application.dto.UserDto
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode

@UseCase
class GetUserDetailUseCase(private val userRepository: UserRepository) {
    fun execute(command: GetUserDetailCommand): UserDto {
        val user = userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(UserErrorCode.USER_NOT_FOUND)

        return UserDto(
            id = user.id!!,
            email = user.email.value,
            name = user.name,
            phone = user.phone.value,
        )
    }
}
