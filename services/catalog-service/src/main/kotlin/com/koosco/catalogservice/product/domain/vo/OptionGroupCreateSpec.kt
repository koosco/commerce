package com.koosco.catalogservice.product.domain.vo

data class OptionGroupCreateSpec(val name: String, val ordering: Int, val options: List<CreateOptionSpec>)

data class CreateOptionSpec(val name: String, val additionalPrice: Long, val ordering: Int)
