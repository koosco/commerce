package com.koosco.userservice.infra.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig(
    @PersistenceContext
    val entityManager: EntityManager,
) {

    @Bean
    fun queryFactory(): JPAQueryFactory = JPAQueryFactory(entityManager)
}
