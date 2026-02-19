package com.koosco.catalogservice.application.command

import org.springframework.data.domain.Pageable

/**
 * fileName       : QueryCommand
 * author         : koo
 * date           : 2025. 12. 25. 오전 1:45
 * description    :
 */
data class FindSkuCommand(val productId: Long, val options: Map<String, String>)

data class GetProductListCommand(val categoryId: Long?, val keyword: String?, val pageable: Pageable)

data class GetProductDetailCommand(val productId: Long)
