package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.BaseException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.CreateAddressCommand
import com.koosco.userservice.application.dto.AddressDto
import com.koosco.userservice.application.port.AddressRepository
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import com.koosco.userservice.domain.entity.Address
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateAddressUseCase(
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository,
) {

    companion object {
        private const val MAX_ADDRESS_COUNT = 10
    }

    @Transactional
    fun execute(command: CreateAddressCommand): AddressDto {
        val member = userRepository.findActiveUserById(command.userId)
            ?: throw NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND)

        val currentCount = addressRepository.countByMemberId(command.userId)
        if (currentCount >= MAX_ADDRESS_COUNT) {
            throw BaseException(MemberErrorCode.ADDRESS_LIMIT_EXCEEDED)
        }

        if (command.isDefault) {
            addressRepository.findByMemberId(command.userId)
                .filter { it.isDefault }
                .forEach { it.clearDefault() }
        }

        val address = Address.create(
            member = member,
            label = command.label,
            recipient = command.recipient,
            phone = command.phone,
            zipCode = command.zipCode,
            address = command.address,
            addressDetail = command.addressDetail,
            isDefault = command.isDefault,
        )

        val saved = addressRepository.save(address)
        return AddressDto.from(saved)
    }
}
