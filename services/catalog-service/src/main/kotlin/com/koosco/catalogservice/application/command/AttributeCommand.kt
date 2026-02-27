package com.koosco.catalogservice.application.command

import com.koosco.catalogservice.domain.enums.AttributeType

data class CreateCategoryAttributeCommand(
    val categoryId: Long,
    val name: String,
    val type: AttributeType,
    val required: Boolean = false,
    val options: String? = null,
    val ordering: Int = 0,
)

data class UpdateCategoryAttributeCommand(
    val attributeId: Long,
    val name: String? = null,
    val required: Boolean? = null,
    val options: String? = null,
    val ordering: Int? = null,
)

data class DeleteCategoryAttributeCommand(val attributeId: Long)

data class GetCategoryAttributesCommand(val categoryId: Long, val includeInherited: Boolean = true)

data class SetProductAttributeValuesCommand(val productId: Long, val attributes: List<AttributeValueSpec>) {
    data class AttributeValueSpec(val attributeId: Long, val value: String)
}
