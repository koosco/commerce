package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.CategoryAttribute
import com.koosco.catalogservice.domain.entity.ProductAttributeValue
import com.koosco.catalogservice.domain.enums.AttributeType

data class CategoryAttributeInfo(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val type: AttributeType,
    val required: Boolean,
    val options: List<String>,
    val ordering: Int,
    val inherited: Boolean = false,
) {
    companion object {
        fun from(attribute: CategoryAttribute, inherited: Boolean = false): CategoryAttributeInfo =
            CategoryAttributeInfo(
                id = attribute.id!!,
                categoryId = attribute.categoryId,
                name = attribute.name,
                type = attribute.type,
                required = attribute.required,
                options = attribute.getOptionList(),
                ordering = attribute.ordering,
                inherited = inherited,
            )
    }
}

data class ProductAttributeValueInfo(
    val id: Long,
    val attributeId: Long,
    val attributeName: String,
    val type: AttributeType,
    val value: String,
) {
    companion object {
        fun from(attributeValue: ProductAttributeValue, attribute: CategoryAttribute): ProductAttributeValueInfo =
            ProductAttributeValueInfo(
                id = attributeValue.id!!,
                attributeId = attributeValue.attributeId,
                attributeName = attribute.name,
                type = attribute.type,
                value = attributeValue.value,
            )
    }
}
