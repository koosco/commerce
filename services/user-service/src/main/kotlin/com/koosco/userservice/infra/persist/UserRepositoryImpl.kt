package com.koosco.userservice.infra.persist

import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.domain.entity.User
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(val jpaUserRepository: JpaUserRepository, val userQuery: UserQuery) : UserRepository {

    override fun save(user: User): User = jpaUserRepository.save(user)

    override fun findActiveUserById(userId: Long): User? = userQuery.findActiveUserById(userId)

    override fun deleteById(userId: Long) {
        jpaUserRepository.deleteById(userId)
    }
}
