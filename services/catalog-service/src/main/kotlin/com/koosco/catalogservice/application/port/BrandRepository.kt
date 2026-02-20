package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.Brand

interface BrandRepository {

    fun save(brand: Brand): Brand

    fun findOrNull(brandId: Long): Brand?

    fun findAll(): List<Brand>

    fun findAllByIdIn(ids: List<Long>): List<Brand>

    fun delete(brand: Brand)
}
