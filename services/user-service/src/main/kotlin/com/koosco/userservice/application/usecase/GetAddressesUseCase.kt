package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.GetAddressesCommand
import com.koosco.userservice.application.dto.AddressDto
import com.koosco.userservice.application.port.AddressRepository
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetAddressesUseCase(
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetAddressesCommand): List<AddressDto> {
        userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND)

        return addressRepository.findByMemberId(command.userId).map { AddressDto.from(it) }
    }
}
