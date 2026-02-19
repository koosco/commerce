package com.koosco.userservice.infra.persist

import com.koosco.userservice.domain.entity.OAuthAccount
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOAuthAccountRepository : JpaRepository<OAuthAccount, Long>
