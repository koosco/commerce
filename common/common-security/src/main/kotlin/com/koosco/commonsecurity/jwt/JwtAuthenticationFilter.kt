package com.koosco.commonsecurity.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)

        if (token != null) {
            when (val result = jwtTokenProvider.validateToken(token)) {
                is TokenValidationResult.Valid -> {
                    val authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                }
                is TokenValidationResult.Expired -> {
                    request.setAttribute(JWT_EXCEPTION_ATTRIBUTE, "Token has expired")
                }
                is TokenValidationResult.Invalid -> {
                    request.setAttribute(JWT_EXCEPTION_ATTRIBUTE, result.reason)
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(jwtProperties.header)
        return if (bearerToken != null && bearerToken.startsWith(jwtProperties.prefix)) {
            bearerToken.substring(jwtProperties.prefix.length)
        } else {
            null
        }
    }

    companion object {
        const val JWT_EXCEPTION_ATTRIBUTE = "jwt_exception"
    }
}
