package com.koosco.inventoryservice.application.port

import com.koosco.inventoryservice.domain.entity.Inventory

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

    /**
     * 일반 구매 재고 차감 (조건부 UPDATE)
     * UPDATE inventory SET total_stock = total_stock - qty WHERE sku_id = :skuId AND total_stock - reserved_stock >= qty
     * @return 영향받은 row 수 (1: 성공, 0: 재고 부족)
     */
    fun deductQuantity(skuId: String, quantity: Int): Int
}
