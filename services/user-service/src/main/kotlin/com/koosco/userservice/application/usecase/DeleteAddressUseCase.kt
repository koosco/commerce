package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.DeleteAddressCommand
import com.koosco.userservice.application.port.AddressRepository
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteAddressUseCase(private val addressRepository: AddressRepository) {

    @Transactional
    fun execute(command: DeleteAddressCommand) {
        val address = addressRepository.findByIdAndMemberId(command.addressId, command.userId)
            ?: throw NotFoundException(
                com.koosco.userservice.common.MemberErrorCode.ADDRESS_NOT_FOUND,
            )

        addressRepository.delete(address)
    }
}
