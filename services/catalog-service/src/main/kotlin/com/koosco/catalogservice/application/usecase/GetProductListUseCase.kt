package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.common.core.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetProductListUseCase(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetProductListCommand): Page<ProductInfo> {
        val page = productRepository.search(command)

        val brandIds = page.content.mapNotNull { it.brandId }.distinct()
        val brandMap = if (brandIds.isNotEmpty()) {
            brandRepository.findAllByIdIn(brandIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        return page.map { ProductInfo.from(it, brandMap[it.brandId]?.name) }
    }
}
