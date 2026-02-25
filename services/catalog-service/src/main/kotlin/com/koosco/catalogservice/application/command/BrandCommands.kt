package com.koosco.catalogservice.application.command

data class CreateBrandCommand(val name: String, val logoImageUrl: String?, val idempotencyKey: String? = null)

data class UpdateBrandCommand(val brandId: Long, val name: String?, val logoImageUrl: String?)

data class DeleteBrandCommand(val brandId: Long)
