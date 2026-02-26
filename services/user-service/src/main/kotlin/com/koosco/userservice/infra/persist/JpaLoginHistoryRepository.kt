package com.koosco.userservice.infra.persist

import com.koosco.userservice.application.port.LoginHistoryRepository
import com.koosco.userservice.domain.entity.LoginHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaLoginHistoryRepository :
    JpaRepository<LoginHistory, Long>,
    LoginHistoryRepository
