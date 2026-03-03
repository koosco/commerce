package com.koosco.searchservice.application.usecase.search

import com.koosco.common.core.annotation.UseCase
import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.application.port.SearchProductRepository
import com.koosco.searchservice.application.result.SearchProductResult
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page

/**
 * 상품 검색 유스케이스.
 * MariaDB FULLTEXT 인덱스를 활용한 검색을 수행한다.
 */
@UseCase
class SearchProductUseCase(private val searchProductRepository: SearchProductRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: SearchProductCommand): Page<SearchProductResult> {
        logger.debug(
            "Searching products: keyword={}, categoryId={}, brandId={}, sort={}",
            command.keyword,
            command.categoryId,
            command.brandId,
            command.sort,
        )

        return searchProductRepository.search(command).map { SearchProductResult.from(it) }
    }
}
