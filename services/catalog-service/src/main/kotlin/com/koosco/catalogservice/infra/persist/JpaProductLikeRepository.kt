package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.ProductLike
import com.koosco.catalogservice.domain.entity.ProductLikeId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductLikeRepository : JpaRepository<ProductLike, ProductLikeId>
