package com.koosco.searchservice.application.port

import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.domain.entity.SearchProduct
import org.springframework.data.domain.Page

/**
 * 검색 상품 조회 포트.
 * FULLTEXT 인덱스를 활용한 상품 검색을 제공한다.
 */
interface SearchProductRepository {

    /**
     * 검색 조건에 따라 상품을 페이징 조회한다.
     */
    fun search(command: SearchProductCommand): Page<SearchProduct>
}
