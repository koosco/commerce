package com.koosco.userservice.domain

import com.koosco.userservice.domain.entity.Address
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Address 도메인 엔티티")
class AddressTest {

    private val member = Member.create(
        email = Email.of("test@example.com"),
        name = "홍길동",
        phone = Phone.of("010-1234-5678"),
        passwordHash = EncryptedPassword.of("encoded"),
    )

    private fun createAddress(isDefault: Boolean = false): Address = Address.create(
        member = member,
        label = "집",
        recipient = "홍길동",
        phone = "010-1234-5678",
        zipCode = "12345",
        address = "서울시 강남구",
        addressDetail = "101호",
        isDefault = isDefault,
    )

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        fun `Address를 생성한다`() {
            val address = createAddress()

            assertThat(address.label).isEqualTo("집")
            assertThat(address.recipient).isEqualTo("홍길동")
            assertThat(address.phone).isEqualTo("010-1234-5678")
            assertThat(address.zipCode).isEqualTo("12345")
            assertThat(address.address).isEqualTo("서울시 강남구")
            assertThat(address.addressDetail).isEqualTo("101호")
            assertThat(address.isDefault).isFalse()
            assertThat(address.deletedAt).isNull()
        }

        @Test
        fun `기본 배송지로 생성한다`() {
            val address = createAddress(isDefault = true)

            assertThat(address.isDefault).isTrue()
        }
    }

    @Nested
    @DisplayName("softDelete")
    inner class SoftDelete {

        @Test
        fun `soft delete하면 deletedAt이 설정된다`() {
            val address = createAddress()

            address.softDelete()

            assertThat(address.deletedAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("clearDefault")
    inner class ClearDefault {

        @Test
        fun `기본 배송지를 해제하면 isDefault가 false가 된다`() {
            val address = createAddress(isDefault = true)

            address.clearDefault()

            assertThat(address.isDefault).isFalse()
        }
    }
}
