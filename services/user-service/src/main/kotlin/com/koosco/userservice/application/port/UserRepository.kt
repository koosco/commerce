package com.koosco.userservice.application.port

import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email

interface UserRepository {

    fun save(member: Member): Member

    fun findActiveUserById(userId: Long): Member?

    fun findByEmail(email: Email): Member?

    fun deleteById(userId: Long)
}
