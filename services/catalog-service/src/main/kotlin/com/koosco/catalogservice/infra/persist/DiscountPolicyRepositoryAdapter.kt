package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.DiscountPolicyRepository
import com.koosco.catalogservice.domain.entity.DiscountPolicy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class DiscountPolicyRepositoryAdapter(private val jpaDiscountPolicyRepository: JpaDiscountPolicyRepository) :
    DiscountPolicyRepository {

    override fun save(discountPolicy: DiscountPolicy): DiscountPolicy = jpaDiscountPolicyRepository.save(discountPolicy)

    override fun findOrNull(id: Long): DiscountPolicy? = jpaDiscountPolicyRepository.findByIdOrNull(id)

    override fun findByProductId(productId: Long): List<DiscountPolicy> =
        jpaDiscountPolicyRepository.findByProductId(productId)

    override fun delete(discountPolicy: DiscountPolicy) = jpaDiscountPolicyRepository.delete(discountPolicy)
}
