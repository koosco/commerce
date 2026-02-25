package com.koosco.userservice.application.port

import com.koosco.userservice.domain.entity.Address

interface AddressRepository {

    fun save(address: Address): Address

    fun findByIdAndMemberId(addressId: Long, memberId: Long): Address?

    fun findByMemberId(memberId: Long): List<Address>

    fun countByMemberId(memberId: Long): Int
}
