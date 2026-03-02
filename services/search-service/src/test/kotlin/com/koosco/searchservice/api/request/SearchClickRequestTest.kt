package com.koosco.searchservice.api.request

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SearchClickRequest 매핑 테스트")
class SearchClickRequestTest {

    @Test
    fun `Command로 변환한다`() {
        val request = SearchClickRequest(
            searchQuery = "스마트폰",
            clickedProductId = 1L,
            clickPosition = 3,
            totalResults = 50,
        )

        val command = request.toCommand(1L)

        assertThat(command.userId).isEqualTo(1L)
        assertThat(command.searchQuery).isEqualTo("스마트폰")
        assertThat(command.clickedProductId).isEqualTo(1L)
        assertThat(command.clickPosition).isEqualTo(3)
        assertThat(command.totalResults).isEqualTo(50)
    }

    @Test
    fun `프로퍼티 접근이 가능하다`() {
        val request = SearchClickRequest("스마트폰", 1L, 3, 50)
        assertThat(request.searchQuery).isEqualTo("스마트폰")
        assertThat(request.clickedProductId).isEqualTo(1L)
        assertThat(request.clickPosition).isEqualTo(3)
        assertThat(request.totalResults).isEqualTo(50)
        assertThat(request.toString()).contains("스마트폰")
        assertThat(request).isEqualTo(SearchClickRequest("스마트폰", 1L, 3, 50))
        assertThat(request.hashCode()).isEqualTo(SearchClickRequest("스마트폰", 1L, 3, 50).hashCode())
    }
}
