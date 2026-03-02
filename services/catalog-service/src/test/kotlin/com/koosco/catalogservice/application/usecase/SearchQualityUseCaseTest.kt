package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.SearchClickCommand
import com.koosco.catalogservice.application.port.SearchClickLogPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("검색 품질 UseCase 테스트")
class SearchQualityUseCaseTest {

    @Mock lateinit var searchClickLogPort: SearchClickLogPort

    @Nested
    @DisplayName("RecordSearchClickUseCase는")
    inner class RecordSearchClickUseCaseTest {

        @Test
        fun `클릭 로그를 저장한다`() {
            val useCase = RecordSearchClickUseCase(searchClickLogPort)
            val command = SearchClickCommand(
                userId = 1L,
                searchQuery = "스마트폰",
                clickedProductId = 100L,
                clickPosition = 3,
                totalResults = 50,
            )

            useCase.execute(command)

            verify(searchClickLogPort).save(command)
        }
    }

    @Nested
    @DisplayName("CalculateSearchQualityUseCase는")
    inner class CalculateSearchQualityUseCaseTest {

        @Test
        fun `검색 쿼리가 없으면 0을 반환한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            whenever(searchClickLogPort.getAllSearchQueries()).thenReturn(emptySet())

            val result = useCase.calculateMapAtK(5)

            assertThat(result).isEqualTo(0.0)
        }

        @Test
        fun `mAP@k를 계산한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            whenever(searchClickLogPort.getAllSearchQueries()).thenReturn(setOf("스마트폰"))
            whenever(searchClickLogPort.getClickPositions("스마트폰")).thenReturn(listOf(1, 3))

            val result = useCase.calculateMapAtK(5)

            assertThat(result).isGreaterThan(0.0)
        }

        @Test
        fun `여러 쿼리의 평균을 계산한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            whenever(searchClickLogPort.getAllSearchQueries()).thenReturn(setOf("스마트폰", "노트북"))
            whenever(searchClickLogPort.getClickPositions("스마트폰")).thenReturn(listOf(1))
            whenever(searchClickLogPort.getClickPositions("노트북")).thenReturn(listOf(2))

            val result = useCase.calculateMapAtK(5)

            assertThat(result).isGreaterThan(0.0)
        }
    }

    @Nested
    @DisplayName("calculateAveragePrecisionAtK는")
    inner class CalculateAPTest {

        @Test
        fun `클릭 위치가 비어있으면 0을 반환한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            val result = useCase.calculateAveragePrecisionAtK(emptyList(), 5)

            assertThat(result).isEqualTo(0.0)
        }

        @Test
        fun `클릭이 k 범위 밖이면 0을 반환한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            val result = useCase.calculateAveragePrecisionAtK(listOf(10, 20), 5)

            assertThat(result).isEqualTo(0.0)
        }

        @Test
        fun `첫 번째 위치에 클릭이 있으면 1점을 반환한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            val result = useCase.calculateAveragePrecisionAtK(listOf(1), 5)

            assertThat(result).isEqualTo(1.0)
        }

        @Test
        fun `여러 위치에 클릭이 있으면 정밀도를 계산한다`() {
            val useCase = CalculateSearchQualityUseCase(searchClickLogPort)

            val result = useCase.calculateAveragePrecisionAtK(listOf(1, 3, 5), 5)

            assertThat(result).isGreaterThan(0.0)
            assertThat(result).isLessThanOrEqualTo(1.0)
        }
    }
}
