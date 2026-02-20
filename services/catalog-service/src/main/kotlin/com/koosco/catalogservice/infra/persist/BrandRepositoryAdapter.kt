package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.domain.entity.Brand
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class BrandRepositoryAdapter(private val jpaBrandRepository: JpaBrandRepository) : BrandRepository {

    override fun save(brand: Brand): Brand = jpaBrandRepository.save(brand)

    override fun findOrNull(brandId: Long): Brand? = jpaBrandRepository.findByIdOrNull(brandId)

    override fun findAll(): List<Brand> = jpaBrandRepository.findAll()

    override fun findAllByIdIn(ids: List<Long>): List<Brand> = jpaBrandRepository.findAllById(ids)

    override fun delete(brand: Brand) = jpaBrandRepository.delete(brand)
}
