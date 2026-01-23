package com.koosco.commonsecurity.resolver

import com.koosco.commonsecurity.jwt.JwtUserPrincipal
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @AuthId 애노테이션이 붙은 파라미터에 SecurityContext의 사용자 ID를 주입하는 ArgumentResolver
 *
 * SecurityContextHolder에서 Authentication을 가져와 userId를 Long으로 추출합니다.
 * 인증 정보가 없거나 userId를 Long으로 변환할 수 없으면 null을 반환합니다.
 */
class AuthIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(AuthId::class.java)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long? {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return null

        val principal = authentication.principal as? JwtUserPrincipal
            ?: return null

        return principal.userId.toLongOrNull()
    }
}
