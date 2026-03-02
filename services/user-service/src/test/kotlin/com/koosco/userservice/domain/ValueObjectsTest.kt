package com.koosco.userservice.domain

import com.koosco.common.core.exception.BaseException
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Value Objects")
class ValueObjectsTest {

    @Nested
    @DisplayName("Email")
    inner class EmailTest {

        @Test
        fun `유효한 이메일로 Email을 생성한다`() {
            val email = Email.of("test@example.com")

            assertThat(email.value).isEqualTo("test@example.com")
        }

        @Test
        fun `null 이메일이면 BaseException이 발생한다`() {
            assertThatThrownBy { Email.of(null) }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `빈 문자열 이메일이면 BaseException이 발생한다`() {
            assertThatThrownBy { Email.of("") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `공백 이메일이면 BaseException이 발생한다`() {
            assertThatThrownBy { Email.of("   ") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `잘못된 형식의 이메일이면 BaseException이 발생한다`() {
            assertThatThrownBy { Email.of("invalid-email") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `isValid는 유효한 이메일에 true를 반환한다`() {
            assertThat(Email.isValid("test@example.com")).isTrue()
        }

        @Test
        fun `isValid는 유효하지 않은 이메일에 false를 반환한다`() {
            assertThat(Email.isValid("invalid")).isFalse()
        }

        @Test
        fun `toString은 이메일 값을 반환한다`() {
            val email = Email.of("test@example.com")

            assertThat(email.toString()).isEqualTo("test@example.com")
        }
    }

    @Nested
    @DisplayName("Phone")
    inner class PhoneTest {

        @Test
        fun `유효한 전화번호로 Phone을 생성한다`() {
            val phone = Phone.of("010-1234-5678")

            assertThat(phone.value).isEqualTo("010-1234-5678")
        }

        @Test
        fun `null 전화번호는 값이 null인 Phone을 생성한다`() {
            val phone = Phone.of(null)

            assertThat(phone.value).isNull()
        }

        @Test
        fun `빈 문자열 전화번호이면 예외가 발생한다`() {
            assertThatThrownBy { Phone.of("") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `공백 전화번호이면 예외가 발생한다`() {
            assertThatThrownBy { Phone.of("   ") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `잘못된 형식의 전화번호이면 예외가 발생한다`() {
            assertThatThrownBy { Phone.of("02-123-4567") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `isValid는 유효한 전화번호에 true를 반환한다`() {
            assertThat(Phone.isValid("010-1234-5678")).isTrue()
        }

        @Test
        fun `isValid는 유효하지 않은 전화번호에 false를 반환한다`() {
            assertThat(Phone.isValid("02-123-4567")).isFalse()
        }

        @Test
        fun `010으로 시작하는 하이픈 없는 번호도 매칭된다`() {
            val phone = Phone.of("010-12345678")

            assertThat(phone.value).isEqualTo("010-12345678")
        }
    }

    @Nested
    @DisplayName("EncryptedPassword")
    inner class EncryptedPasswordTest {

        @Test
        fun `유효한 문자열로 EncryptedPassword를 생성한다`() {
            val password = EncryptedPassword.of("hashedPassword123")

            assertThat(password.value).isEqualTo("hashedPassword123")
        }

        @Test
        fun `null이면 예외가 발생한다`() {
            assertThatThrownBy { EncryptedPassword.of(null) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `빈 문자열이면 예외가 발생한다`() {
            assertThatThrownBy { EncryptedPassword.of("") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `공백 문자열이면 예외가 발생한다`() {
            assertThatThrownBy { EncryptedPassword.of("   ") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `생성자로 직접 생성할 수 있다`() {
            val password = EncryptedPassword("hashedValue")

            assertThat(password.value).isEqualTo("hashedValue")
        }

        @Test
        fun `생성자에 빈 문자열이면 예외가 발생한다`() {
            assertThatThrownBy { EncryptedPassword("") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `생성자에 공백 문자열이면 예외가 발생한다`() {
            assertThatThrownBy { EncryptedPassword("   ") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
