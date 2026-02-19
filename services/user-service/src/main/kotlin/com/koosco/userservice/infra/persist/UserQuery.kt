package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.entity.QMember.member
import com.koosco.userservice.domain.enums.MemberStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserQuery(private val queryFactory: JPAQueryFactory) {
    fun findActiveUserById(userId: Long): Member? = queryFactory
        .selectFrom(member)
        .where(
            member.id.eq(userId)
                .and(member.status.eq(MemberStatus.ACTIVE)),
        )
        .fetchOne()
}
