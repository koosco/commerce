package com.koosco.userservice.infra.persist

import com.koosco.userservice.application.port.UserIdempotencyRepository
import com.koosco.userservice.domain.entity.UserIdempotency
import org.springframework.stereotype.Repository

@Repository
class UserIdempotencyRepositoryAdapter(private val jpaRepository: JpaUserIdempotencyRepository) :
    UserIdempotencyRepository {

    override fun findByIdempotencyKeyAndResourceType(idempotencyKey: String, resourceType: String): UserIdempotency? =
        jpaRepository.findByIdempotencyKeyAndResourceType(idempotencyKey, resourceType)

    override fun save(entry: UserIdempotency): UserIdempotency = jpaRepository.save(entry)
}
