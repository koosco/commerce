package com.koosco.userservice.api

import com.koosco.userservice.api.controller.AddressController
import com.koosco.userservice.application.command.DeleteAddressCommand
import com.koosco.userservice.application.command.GetAddressesCommand
import com.koosco.userservice.application.dto.AddressDto
import com.koosco.userservice.application.usecase.CreateAddressUseCase
import com.koosco.userservice.application.usecase.DeleteAddressUseCase
import com.koosco.userservice.application.usecase.GetAddressesUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("AddressController")
class AddressControllerTest {

    private val getAddressesUseCase: GetAddressesUseCase = mock()
    private val createAddressUseCase: CreateAddressUseCase = mock()
    private val deleteAddressUseCase: DeleteAddressUseCase = mock()

    private val controller = AddressController(getAddressesUseCase, createAddressUseCase, deleteAddressUseCase)

    @Nested
    @DisplayName("GET /api/users/me/addresses")
    inner class GetAddresses {

        @Test
        fun `주소 목록을 조회한다`() {
            val addresses = listOf(
                AddressDto(1L, "집", "홍길동", "010-1234-5678", "12345", "서울시", "101호", false),
                AddressDto(2L, "회사", "홍길동", "010-1234-5678", "67890", "서울시", "202호", true),
            )
            whenever(getAddressesUseCase.execute(GetAddressesCommand(1L))).thenReturn(addresses)

            val result = controller.getAddresses(1L)

            assertThat(result.success).isTrue()
        }
    }

    @Nested
    @DisplayName("POST /api/users/me/addresses")
    inner class CreateAddress {

        @Test
        fun `주소를 생성한다`() {
            val request = CreateAddressRequest(
                label = "집",
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시 강남구",
                addressDetail = "101호",
                isDefault = false,
            )
            val addressDto = AddressDto(1L, "집", "홍길동", "010-1234-5678", "12345", "서울시 강남구", "101호", false)

            whenever(createAddressUseCase.execute(any())).thenReturn(addressDto)

            val result = controller.createAddress(1L, request)

            assertThat(result.success).isTrue()
            verify(createAddressUseCase).execute(argThat { userId == 1L && label == "집" })
        }

        @Test
        fun `멱등성 키가 포함된 주소 생성 요청을 처리한다`() {
            val request = CreateAddressRequest(
                label = "집",
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시 강남구",
                addressDetail = "101호",
                isDefault = false,
                idempotencyKey = "idem-key",
            )
            val addressDto = AddressDto(1L, "집", "홍길동", "010-1234-5678", "12345", "서울시 강남구", "101호", false)

            whenever(createAddressUseCase.execute(any())).thenReturn(addressDto)

            val result = controller.createAddress(1L, request)

            assertThat(result.success).isTrue()
            verify(createAddressUseCase).execute(argThat { idempotencyKey == "idem-key" })
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/me/addresses/{addressId}")
    inner class DeleteAddress {

        @Test
        fun `주소를 삭제한다`() {
            val result = controller.deleteAddress(1L, 10L)

            assertThat(result.success).isTrue()
            verify(deleteAddressUseCase).execute(DeleteAddressCommand(1L, 10L))
        }
    }
}
