package com.koosco.commonsecurity.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JWT 검증을 위한 설정 프로퍼티
 * auth-service와 동일한 secret을 사용해야 토큰 검증이 가능합니다.
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    /**
     * JWT 서명 검증을 위한 비밀키 (auth-service와 동일해야 함)
     */
    val secret: String,

    /**
     * Authorization 헤더 이름
     */
    val header: String = "Authorization",

    /**
     * Bearer 토큰 접두사
     */
    val prefix: String = "Bearer ",
)
