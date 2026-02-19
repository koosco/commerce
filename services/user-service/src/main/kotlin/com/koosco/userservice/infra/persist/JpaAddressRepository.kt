package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.Address
import org.springframework.data.jpa.repository.JpaRepository

interface JpaAddressRepository : JpaRepository<Address, Long> {

    fun findByMemberId(memberId: Long): List<Address>

    fun countByMemberId(memberId: Long): Int
}
