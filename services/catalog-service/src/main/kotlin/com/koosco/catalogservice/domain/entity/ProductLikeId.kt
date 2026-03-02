package com.koosco.catalogservice.domain.entity

import java.io.Serializable

data class ProductLikeId(val productId: Long = 0, val userId: Long = 0) : Serializable
