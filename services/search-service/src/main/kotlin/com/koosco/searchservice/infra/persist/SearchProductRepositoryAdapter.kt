package com.koosco.searchservice.infra.persist

import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.application.port.SearchProductRepository
import com.koosco.searchservice.domain.entity.SearchProduct
import com.koosco.searchservice.domain.enums.SearchSortStrategy
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

/**
 * MariaDB FULLTEXT 인덱스를 활용한 검색 저장소 구현체.
 * Native Query를 사용하여 MATCH AGAINST 구문을 실행한다.
 */
@Repository
class SearchProductRepositoryAdapter(@PersistenceContext private val entityManager: EntityManager) :
    SearchProductRepository {

    override fun search(command: SearchProductCommand): Page<SearchProduct> {
        val hasKeyword = !command.keyword.isNullOrBlank()
        val queryBuilder = StringBuilder()
        val countQueryBuilder = StringBuilder()
        val params = mutableMapOf<String, Any>()

        // SELECT 절
        if (hasKeyword) {
            queryBuilder.append(
                "SELECT sp.*, MATCH(sp.name, sp.description) AGAINST(:keyword IN BOOLEAN MODE) AS relevance_score ",
            )
        } else {
            queryBuilder.append("SELECT sp.* ")
        }
        queryBuilder.append("FROM search_product sp ")
        countQueryBuilder.append("SELECT COUNT(*) FROM search_product sp ")

        // WHERE 절
        val conditions = mutableListOf<String>()
        conditions.add("sp.status = :status")
        params["status"] = command.status

        if (hasKeyword) {
            conditions.add("MATCH(sp.name, sp.description) AGAINST(:keyword IN BOOLEAN MODE)")
            params["keyword"] = command.keyword!!
        }
        command.categoryId?.let {
            conditions.add("sp.category_id = :categoryId")
            params["categoryId"] = it
        }
        command.brandId?.let {
            conditions.add("sp.brand_id = :brandId")
            params["brandId"] = it
        }
        command.minPrice?.let {
            conditions.add("sp.selling_price >= :minPrice")
            params["minPrice"] = it
        }
        command.maxPrice?.let {
            conditions.add("sp.selling_price <= :maxPrice")
            params["maxPrice"] = it
        }

        val whereClause = "WHERE " + conditions.joinToString(" AND ")
        queryBuilder.append(whereClause)
        countQueryBuilder.append(whereClause)

        // ORDER BY 절
        queryBuilder.append(" ")
        queryBuilder.append(buildOrderBy(command.sort, hasKeyword))

        // 페이징
        queryBuilder.append(" LIMIT :limit OFFSET :offset")

        // 데이터 쿼리 실행
        val query = entityManager.createNativeQuery(queryBuilder.toString(), SearchProduct::class.java)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        query.setParameter("limit", command.pageable.pageSize)
        query.setParameter("offset", command.pageable.offset)

        @Suppress("UNCHECKED_CAST")
        val content = query.resultList as List<SearchProduct>

        // 카운트 쿼리
        val countQuery = entityManager.createNativeQuery(countQueryBuilder.toString())
        params.forEach { (key, value) -> countQuery.setParameter(key, value) }

        return PageableExecutionUtils.getPage(content, command.pageable) {
            (countQuery.singleResult as Number).toLong()
        }
    }

    private fun buildOrderBy(sort: SearchSortStrategy, hasKeyword: Boolean): String = when (sort) {
        SearchSortStrategy.RELEVANCE -> {
            if (hasKeyword) {
                "ORDER BY relevance_score DESC, sp.created_at DESC"
            } else {
                "ORDER BY sp.created_at DESC"
            }
        }
        SearchSortStrategy.LATEST -> "ORDER BY sp.created_at DESC"
        SearchSortStrategy.PRICE_ASC -> "ORDER BY sp.selling_price ASC"
        SearchSortStrategy.PRICE_DESC -> "ORDER BY sp.selling_price DESC"
        SearchSortStrategy.POPULARITY -> {
            "ORDER BY (sp.sales_count * $WEIGHT_SALES + sp.view_count * $WEIGHT_VIEW + " +
                "sp.like_count * $WEIGHT_LIKE) DESC"
        }
    }

    companion object {
        private const val WEIGHT_SALES = 0.5
        private const val WEIGHT_VIEW = 0.2
        private const val WEIGHT_LIKE = 0.3
    }
}
