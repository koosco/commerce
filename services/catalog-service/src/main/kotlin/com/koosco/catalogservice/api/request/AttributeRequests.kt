package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateCategoryAttributeCommand
import com.koosco.catalogservice.application.command.SetProductAttributeValuesCommand
import com.koosco.catalogservice.application.command.UpdateCategoryAttributeCommand
import com.koosco.catalogservice.domain.enums.AttributeType
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateCategoryAttributeRequest(
    @field:NotBlank(message = "속성 이름은 필수입니다.")
    val name: String,

    @field:NotNull(message = "속성 타입은 필수입니다.")
    val type: AttributeType,

    val required: Boolean = false,

    val options: String? = null,

    @field:Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
    val ordering: Int = 0,
) {
    fun toCommand(categoryId: Long): CreateCategoryAttributeCommand = CreateCategoryAttributeCommand(
        categoryId = categoryId,
        name = name,
        type = type,
        required = required,
        options = options,
        ordering = ordering,
    )
}

data class UpdateCategoryAttributeRequest(
    val name: String? = null,
    val required: Boolean? = null,
    val options: String? = null,
    @field:Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
    val ordering: Int? = null,
) {
    fun toCommand(attributeId: Long): UpdateCategoryAttributeCommand = UpdateCategoryAttributeCommand(
        attributeId = attributeId,
        name = name,
        required = required,
        options = options,
        ordering = ordering,
    )
}

data class SetProductAttributeValuesRequest(
    @field:Valid
    val attributes: List<AttributeValueRequest>,
) {
    data class AttributeValueRequest(
        @field:NotNull(message = "속성 ID는 필수입니다.")
        val attributeId: Long,

        @field:NotBlank(message = "속성 값은 필수입니다.")
        val value: String,
    )

    fun toCommand(productId: Long): SetProductAttributeValuesCommand = SetProductAttributeValuesCommand(
        productId = productId,
        attributes = attributes.map {
            SetProductAttributeValuesCommand.AttributeValueSpec(
                attributeId = it.attributeId,
                value = it.value,
            )
        },
    )
}
