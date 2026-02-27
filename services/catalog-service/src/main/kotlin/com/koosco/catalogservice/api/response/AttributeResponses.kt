package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.application.result.ProductAttributeValueInfo
import com.koosco.catalogservice.domain.enums.AttributeType

data class CategoryAttributeResponse(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val type: AttributeType,
    val required: Boolean,
    val options: List<String>,
    val ordering: Int,
    val inherited: Boolean,
) {
    companion object {
        fun from(info: CategoryAttributeInfo): CategoryAttributeResponse = CategoryAttributeResponse(
            id = info.id,
            categoryId = info.categoryId,
            name = info.name,
            type = info.type,
            required = info.required,
            options = info.options,
            ordering = info.ordering,
            inherited = info.inherited,
        )
    }
}

data class ProductAttributeValueResponse(
    val id: Long,
    val attributeId: Long,
    val attributeName: String,
    val type: AttributeType,
    val value: String,
) {
    companion object {
        fun from(info: ProductAttributeValueInfo): ProductAttributeValueResponse = ProductAttributeValueResponse(
            id = info.id,
            attributeId = info.attributeId,
            attributeName = info.attributeName,
            type = info.type,
            value = info.value,
        )
    }
}
