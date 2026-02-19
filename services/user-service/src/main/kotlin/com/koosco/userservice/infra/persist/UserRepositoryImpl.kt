package com.koosco.userservice.infra.persist

import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(val jpaUserRepository: JpaUserRepository, val userQuery: UserQuery) : UserRepository {

    override fun save(member: Member): Member = jpaUserRepository.save(member)

    override fun findActiveUserById(userId: Long): Member? = userQuery.findActiveUserById(userId)

    override fun findByEmail(email: Email): Member? = jpaUserRepository.findByEmail(email)

    override fun deleteById(userId: Long) {
        jpaUserRepository.deleteById(userId)
    }
}
