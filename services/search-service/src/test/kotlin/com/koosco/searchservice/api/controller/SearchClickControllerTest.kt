package com.koosco.searchservice.api.controller

import com.koosco.searchservice.api.request.SearchClickRequest
import com.koosco.searchservice.application.usecase.search.RecordSearchClickUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
@DisplayName("SearchClickController 테스트")
class SearchClickControllerTest {

    @Mock
    lateinit var recordSearchClickUseCase: RecordSearchClickUseCase

    @Test
    fun `검색 클릭을 기록한다`() {
        val controller = SearchClickController(recordSearchClickUseCase)
        val request = SearchClickRequest(
            searchQuery = "스마트폰",
            clickedProductId = 1L,
            clickPosition = 3,
            totalResults = 50,
        )

        val response = controller.recordSearchClick(request, 1L)

        verify(recordSearchClickUseCase).execute(any())
        assertThat(response).isNotNull
    }
}
