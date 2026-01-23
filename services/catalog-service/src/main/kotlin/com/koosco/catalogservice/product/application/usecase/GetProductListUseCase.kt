package com.koosco.catalogservice.product.application.usecase

import com.koosco.catalogservice.product.application.command.GetProductListCommand
import com.koosco.catalogservice.product.application.port.ProductRepository
import com.koosco.catalogservice.product.application.result.ProductInfo
import com.koosco.catalogservice.product.domain.enums.ProductStatus
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
