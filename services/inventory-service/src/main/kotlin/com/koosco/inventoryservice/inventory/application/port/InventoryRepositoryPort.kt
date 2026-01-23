package com.koosco.inventoryservice.inventory.application.port

import com.koosco.inventoryservice.inventory.domain.entity.Inventory

interface InventoryRepositoryPort {

    fun save(inventory: Inventory)

    fun saveAll(inventories: List<Inventory>)

    fun findBySkuIdOrNull(skuId: String): Inventory?

    fun findForUpdate(skuId: String): Inventory?

    fun findAllBySkuIdIn(skuIds: List<String>): List<Inventory>

    /**
     * Pessimistic write lock을 획득하면서 재고 조회
     * 트랜잭션이 종료될 때까지 다른 트랜잭션의 읽기/쓰기 차단
     */
    fun findAllBySkuIdInWithLock(skuIds: List<String>): List<Inventory>

    fun existsBySkuId(skuId: String): Boolean
}
