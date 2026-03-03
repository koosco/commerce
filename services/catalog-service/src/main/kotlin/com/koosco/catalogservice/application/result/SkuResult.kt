package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.ProductSku

data class SkuResult(val sku: ProductSku, val available: Boolean)
