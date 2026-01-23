package com.koosco.commonsecurity.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

/**
 * JWT 토큰 검증 및 인증 정보 추출을 담당하는 Provider
 * 토큰 발급은 auth-service에서 담당하며, 이 모듈은 검증 전용입니다.
 */
@Component
class JwtTokenProvider(private val jwtProperties: JwtProperties) {

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     */
    fun validateToken(token: String): TokenValidationResult = try {
        val claims = parseClaims(token)
        TokenValidationResult.Valid(claims)
    } catch (e: ExpiredJwtException) {
        TokenValidationResult.Expired
    } catch (e: JwtException) {
        TokenValidationResult.Invalid(e.message ?: "Invalid token")
    } catch (e: IllegalArgumentException) {
        TokenValidationResult.Invalid("Token is empty or malformed")
    }

    /**
     * 토큰에서 Spring Security Authentication 객체를 생성합니다.
     */
    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val userId = claims.subject
        val roles = getRolesFromClaims(claims)

        val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        val principal = JwtUserPrincipal(userId, roles)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     */
    fun getUserId(token: String): String = parseClaims(token).subject

    /**
     * 토큰에서 사용자 권한 목록을 추출합니다.
     */
    fun getRoles(token: String): List<String> = getRolesFromClaims(parseClaims(token))

    /**
     * 토큰에서 모든 Claims를 추출합니다.
     */
    fun getClaims(token: String): Claims = parseClaims(token)

    private fun parseClaims(token: String): Claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload

    @Suppress("UNCHECKED_CAST")
    private fun getRolesFromClaims(claims: Claims): List<String> = claims[ROLES_CLAIM] as? List<String> ?: emptyList()

    companion object {
        private const val ROLES_CLAIM = "roles"
    }
}

sealed class TokenValidationResult {
    data class Valid(val claims: Claims) : TokenValidationResult()

    data object Expired : TokenValidationResult()

    data class Invalid(val reason: String) : TokenValidationResult()
}
