package com.koosco.commonsecurity.config

import com.koosco.commonsecurity.resolver.AuthIdArgumentResolver
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @Auth ArgumentResolver를 자동으로 등록하는 AutoConfiguration
 *
 * 다른 프로젝트에서 common-security를 의존성으로 추가하면
 * 별도 설정 없이 @Auth 애노테이션을 사용할 수 있습니다.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class WebMvcAutoConfiguration : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthIdArgumentResolver())
    }
}
