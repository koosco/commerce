package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaProductRepository : JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.optionGroups WHERE p.id = :id")
    fun findByIdWithOptions(@Param("id") id: Long): Product?

    @Query(
        "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.discountPolicies WHERE p.id = :id",
    )
    fun findByIdWithDiscountPolicies(@Param("id") id: Long): Product?

    @Query("SELECT p FROM Product p JOIN p.skus s WHERE s.skuId = :skuId")
    fun findBySkuId(@Param("skuId") skuId: String): Product?
}
