package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.ReviewLike
import com.koosco.catalogservice.domain.entity.ReviewLikeId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaReviewLikeRepository : JpaRepository<ReviewLike, ReviewLikeId>
