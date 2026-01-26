package com.koosco.userservice.application.port

import com.koosco.userservice.domain.entity.User

interface UserRepository {

    fun save(user: User): User

    fun findActiveUserById(userId: Long): User?

    fun deleteById(userId: Long)
}
