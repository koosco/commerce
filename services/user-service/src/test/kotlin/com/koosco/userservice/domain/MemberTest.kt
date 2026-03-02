package com.koosco.userservice.domain

import com.koosco.common.core.exception.ConflictException
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.enums.MemberRole
import com.koosco.userservice.domain.enums.MemberStatus
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Member 도메인 엔티티")
class MemberTest {

    private fun createMember(
        email: String = "test@example.com",
        name: String = "홍길동",
        phone: String? = "010-1234-5678",
        password: String? = "encodedPassword",
    ): Member = Member.create(
        email = Email.of(email),
        name = name,
        phone = Phone.of(phone),
        passwordHash = password?.let { EncryptedPassword.of(it) },
    )

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        fun `Member를 생성하면 ACTIVE 상태와 USER 역할을 가진다`() {
            val member = createMember()

            assertThat(member.email.value).isEqualTo("test@example.com")
            assertThat(member.name).isEqualTo("홍길동")
            assertThat(member.phone?.value).isEqualTo("010-1234-5678")
            assertThat(member.passwordHash?.value).isEqualTo("encodedPassword")
            assertThat(member.role).isEqualTo(MemberRole.USER)
            assertThat(member.status).isEqualTo(MemberStatus.ACTIVE)
        }

        @Test
        fun `phone이 null이면 phone 없이 생성된다`() {
            val member = createMember(phone = null)

            assertThat(member.phone?.value).isNull()
        }

        @Test
        fun `passwordHash가 null이면 비밀번호 없이 생성된다`() {
            val member = createMember(password = null)

            assertThat(member.passwordHash).isNull()
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        fun `name과 phone을 수정하면 변경된다`() {
            val member = createMember()

            member.update("김철수", Phone.of("010-9999-8888"))

            assertThat(member.name).isEqualTo("김철수")
            assertThat(member.phone?.value).isEqualTo("010-9999-8888")
        }

        @Test
        fun `name이 null이면 기존 이름이 유지된다`() {
            val member = createMember()

            member.update(null, Phone.of("010-9999-8888"))

            assertThat(member.name).isEqualTo("홍길동")
            assertThat(member.phone?.value).isEqualTo("010-9999-8888")
        }

        @Test
        fun `phone이 null이면 기존 전화번호가 유지된다`() {
            val member = createMember()

            member.update("김철수", null)

            assertThat(member.name).isEqualTo("김철수")
            assertThat(member.phone?.value).isEqualTo("010-1234-5678")
        }
    }

    @Nested
    @DisplayName("withdraw")
    inner class Withdraw {

        @Test
        fun `ACTIVE 상태에서 탈퇴하면 WITHDRAWN 상태가 된다`() {
            val member = createMember()

            member.withdraw()

            assertThat(member.status).isEqualTo(MemberStatus.WITHDRAWN)
        }

        @Test
        fun `이미 WITHDRAWN 상태에서 탈퇴하면 ConflictException이 발생한다`() {
            val member = createMember()
            member.withdraw()

            assertThatThrownBy { member.withdraw() }
                .isInstanceOf(ConflictException::class.java)
        }
    }

    @Nested
    @DisplayName("lock")
    inner class Lock {

        @Test
        fun `ACTIVE 상태에서 잠금하면 LOCKED 상태가 된다`() {
            val member = createMember()

            member.lock()

            assertThat(member.status).isEqualTo(MemberStatus.LOCKED)
        }

        @Test
        fun `이미 LOCKED 상태에서 잠금하면 ConflictException이 발생한다`() {
            val member = createMember()
            member.lock()

            assertThatThrownBy { member.lock() }
                .isInstanceOf(ConflictException::class.java)
        }
    }
}
