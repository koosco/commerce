package com.koosco.catalogservice.domain.entity

import java.io.Serializable

data class ReviewLikeId(val reviewId: Long = 0, val userId: Long = 0) : Serializable
