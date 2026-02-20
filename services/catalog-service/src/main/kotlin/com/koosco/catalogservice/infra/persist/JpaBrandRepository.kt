package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBrandRepository : JpaRepository<Brand, Long>
