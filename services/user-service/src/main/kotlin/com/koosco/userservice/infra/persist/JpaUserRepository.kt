package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long>
