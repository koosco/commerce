package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductSku
import org.springframework.stereotype.Service

@Service
class SkuGenerator {

    /**
     * 옵션 조합을 생성하여 모든 가능한 SKU를 생성합니다.
     * 예: 색상(빨강, 파랑) x 사이즈(S, M) -> 4개의 SKU
     */
    fun generateSkus(product: Product) {
        if (product.optionGroups.isEmpty()) {
            return
        }

        val options = product.optionGroups
            .sortedBy { it.ordering }
            .map { group ->
                group.options
                    .sortedBy { it.ordering }
                    .map { option -> group.name to option }
            }

        val combinations = cartesianProduct(options)

        val skus = combinations.map { combination ->
            val optionsMap = combination.associate { (groupName, option) ->
                groupName to option.name
            }
            val skuPrice =
                product.price + combination.sumOf { (_, option) -> option.additionalPrice }

            ProductSku.create(
                product = product,
                options = optionsMap,
                price = skuPrice,
            )
        }

        product.addSkus(skus)
    }

    private fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> {
        if (lists.isEmpty()) return listOf(emptyList())
        if (lists.size == 1) return lists[0].map { listOf(it) }

        val result = mutableListOf<List<T>>()
        val rest = cartesianProduct(lists.drop(1))

        for (item in lists[0]) {
            for (r in rest) {
                result.add(listOf(item) + r)
            }
        }

        return result
    }
}
