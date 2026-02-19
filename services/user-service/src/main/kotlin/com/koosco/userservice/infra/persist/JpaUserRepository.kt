package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: Email): Member?
}
