package com.koosco.authservice.infra.security

import com.koosco.authservice.application.dto.AuthTokenDto
import com.koosco.authservice.application.port.TokenGeneratorPort
import com.koosco.authservice.infra.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenGeneratorAdapter(private val jwtProperties: JwtProperties) : TokenGeneratorPort {
    private val secretKey: SecretKey = run {
        require(jwtProperties.secret.isNotBlank()) {
            "JWT secret cannot be empty. Please set JWT_SECRET environment variable or use the default in application.yaml"
        }

        val secretBytes = jwtProperties.secret.toByteArray()
        require(secretBytes.size >= 32) {
            "JWT secret must be at least 256 bits (32 bytes). Current size: ${secretBytes.size * 8} bits. " +
                "Please use a longer secret or generate one using: Jwts.SIG.HS256.key().build()"
        }
        Keys.hmacShaKeyFor(secretBytes)
    }

    override fun generateTokens(userId: Long, email: String, roles: List<String>): AuthTokenDto {
        val now = Date()
        val accessTokenExpiration = Date(now.time + jwtProperties.expiration * 1000)
        val refreshTokenExpiration = Date(now.time + jwtProperties.refreshExpiration * 1000)

        val accessToken = Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("roles", roles)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(accessTokenExpiration)
            .signWith(secretKey)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(refreshTokenExpiration)
            .signWith(secretKey)
            .compact()

        return AuthTokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            refreshTokenExpiresIn = jwtProperties.refreshExpiration,
        )
    }

    override fun validateRefreshToken(token: String): Boolean = try {
        val claims = parseClaims(token)
        claims["type"] == "refresh" && !isTokenExpired(claims)
    } catch (e: Exception) {
        false
    }

    private fun parseClaims(token: String): Claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload

    private fun isTokenExpired(claims: Claims): Boolean = claims.expiration.before(Date())
}
