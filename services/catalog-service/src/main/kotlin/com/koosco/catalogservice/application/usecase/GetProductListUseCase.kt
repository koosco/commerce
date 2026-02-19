package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.common.core.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetProductListUseCase(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun execute(command: GetProductListCommand): Page<ProductInfo> = productRepository.findByConditions(
        categoryId = command.categoryId,
        keyword = command.keyword,
        status = ProductStatus.ACTIVE,
        pageable = command.pageable,
    ).map { ProductInfo.from(it) }
}
