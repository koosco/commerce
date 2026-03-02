package com.koosco.userservice.domain

import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.entity.OAuthAccount
import com.koosco.userservice.domain.enums.OAuthProvider
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("OAuthAccount")
class OAuthAccountTest {

    @Test
    fun `OAuthAccount를 생성한다`() {
        val member = Member.create(
            email = Email.of("test@example.com"),
            name = "홍길동",
            phone = Phone.of("010-1234-5678"),
            passwordHash = null,
        )

        val oauthAccount = OAuthAccount(
            member = member,
            provider = OAuthProvider.KAKAO,
            providerUserId = "kakao-12345",
        )

        assertThat(oauthAccount.id).isNull()
        assertThat(oauthAccount.member).isEqualTo(member)
        assertThat(oauthAccount.provider).isEqualTo(OAuthProvider.KAKAO)
        assertThat(oauthAccount.providerUserId).isEqualTo("kakao-12345")
        assertThat(oauthAccount.createdAt).isNotNull()
    }

    @Test
    fun `다양한 OAuth 제공자로 계정을 생성할 수 있다`() {
        val member = Member.create(
            email = Email.of("test@example.com"),
            name = "홍길동",
            phone = null,
            passwordHash = null,
        )

        val googleAccount = OAuthAccount(
            member = member,
            provider = OAuthProvider.GOOGLE,
            providerUserId = "google-67890",
        )

        val appleAccount = OAuthAccount(
            member = member,
            provider = OAuthProvider.APPLE,
            providerUserId = "apple-11111",
        )

        assertThat(googleAccount.provider).isEqualTo(OAuthProvider.GOOGLE)
        assertThat(appleAccount.provider).isEqualTo(OAuthProvider.APPLE)
    }
}
