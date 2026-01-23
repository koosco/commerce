package com.koosco.inventoryservice.inventory.infra.persist

import com.koosco.inventoryservice.inventory.application.port.InventoryRepositoryPort
import com.koosco.inventoryservice.inventory.domain.entity.Inventory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class InventoryRepositoryImpl(
    private val jpaInventoryRepository: JpaInventoryRepository,
    private val inventoryQuery: InventoryQuery,
) : InventoryRepositoryPort {

    override fun save(inventory: Inventory) {
        jpaInventoryRepository.save(inventory)
    }

    override fun saveAll(inventories: List<Inventory>) {
        jpaInventoryRepository.saveAll(inventories)
    }

    override fun findBySkuIdOrNull(skuId: String): Inventory? = jpaInventoryRepository.findByIdOrNull(skuId)

    override fun findForUpdate(skuId: String): Inventory? = jpaInventoryRepository.findByIdWithLock(skuId)

    override fun findAllBySkuIdIn(skuIds: List<String>): List<Inventory> = inventoryQuery.findAllBySkuIdIn(skuIds)

    override fun findAllBySkuIdInWithLock(skuIds: List<String>): List<Inventory> =
        jpaInventoryRepository.findAllBySkuIdInWithLock(skuIds)

    override fun existsBySkuId(skuId: String): Boolean = jpaInventoryRepository.existsById(skuId)
}
