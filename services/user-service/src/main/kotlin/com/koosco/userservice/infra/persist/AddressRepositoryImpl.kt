package com.koosco.userservice.infra.persist

import com.koosco.userservice.application.port.AddressRepository
import com.koosco.userservice.domain.entity.Address
import org.springframework.stereotype.Repository

@Repository
class AddressRepositoryImpl(private val jpaAddressRepository: JpaAddressRepository) : AddressRepository {

    override fun save(address: Address): Address = jpaAddressRepository.save(address)

    override fun findByMemberId(memberId: Long): List<Address> = jpaAddressRepository.findByMemberId(memberId)

    override fun countByMemberId(memberId: Long): Int = jpaAddressRepository.countByMemberId(memberId)
}
