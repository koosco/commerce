package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.SearchClickLogPort
import com.koosco.common.core.annotation.UseCase
import org.slf4j.LoggerFactory

/**
 * 검색 품질 지표 mAP@k를 계산하는 유스케이스.
 *
 * mAP@k (Mean Average Precision at k):
 * - 각 검색 쿼리별로 AP@k (Average Precision at k)를 계산
 * - 전체 쿼리의 평균을 구해 mAP@k를 산출
 *
 * AP@k = (1/min(m,k)) * sum(Precision@i * rel(i)) for i=1..k
 * - m: 전체 관련 문서 수 (클릭된 상품 수)
 * - rel(i): i번째 결과가 관련 있으면 1, 아니면 0
 * - Precision@i: 상위 i개 결과 중 관련 문서 비율
 */
@UseCase
class CalculateSearchQualityUseCase(private val searchClickLogPort: SearchClickLogPort) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 전체 검색 쿼리에 대한 mAP@k를 계산한다.
     *
     * @param k 상위 k개 결과까지만 평가
     * @return mAP@k 값 (0.0 ~ 1.0)
     */
    fun calculateMapAtK(k: Int): Double {
        val queries = searchClickLogPort.getAllSearchQueries()
        if (queries.isEmpty()) return 0.0

        val totalAp = queries.sumOf { query ->
            val clickPositions = searchClickLogPort.getClickPositions(query)
            calculateAveragePrecisionAtK(clickPositions, k)
        }

        val mapAtK = totalAp / queries.size
        logger.debug("Calculated mAP@{}: {} (queries={})", k, mapAtK, queries.size)
        return mapAtK
    }

    /**
     * 단일 쿼리에 대한 AP@k를 계산한다.
     *
     * @param clickPositions 클릭된 위치 목록 (1-based, 정렬됨)
     * @param k 상위 k개 결과까지만 평가
     * @return AP@k 값 (0.0 ~ 1.0)
     */
    internal fun calculateAveragePrecisionAtK(clickPositions: List<Int>, k: Int): Double {
        if (clickPositions.isEmpty()) return 0.0

        val relevantInTopK = clickPositions.filter { it in 1..k }.toSet()
        if (relevantInTopK.isEmpty()) return 0.0

        var hits = 0
        var sumPrecision = 0.0

        for (i in 1..k) {
            if (i in relevantInTopK) {
                hits++
                sumPrecision += hits.toDouble() / i
            }
        }

        val m = clickPositions.size
        return sumPrecision / minOf(m, k)
    }
}
