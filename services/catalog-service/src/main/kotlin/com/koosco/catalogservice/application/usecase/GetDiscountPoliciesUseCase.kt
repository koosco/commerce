package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetDiscountPoliciesCommand
import com.koosco.catalogservice.application.port.DiscountPolicyRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.DiscountPolicyResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetDiscountPoliciesUseCase(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetDiscountPoliciesCommand): List<DiscountPolicyResult> {
        productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        return discountPolicyRepository.findByProductId(command.productId)
            .map { DiscountPolicyResult.from(it) }
    }
}
