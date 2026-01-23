package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.QUser.user
import com.koosco.userservice.domain.entity.User
import com.koosco.userservice.domain.enums.UserStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserQuery(private val queryFactory: JPAQueryFactory) {
    fun findActiveUserById(userId: Long): User? = queryFactory
        .selectFrom(user)
        .where(
            user.id.eq(userId)
                .and(user.status.eq(UserStatus.ACTIVE)),
        )
        .fetchOne()
}
