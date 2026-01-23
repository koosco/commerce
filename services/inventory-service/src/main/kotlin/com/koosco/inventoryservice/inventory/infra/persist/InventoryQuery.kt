package com.koosco.inventoryservice.inventory.infra.persist

import com.koosco.inventoryservice.inventory.domain.entity.Inventory
import com.koosco.inventoryservice.inventory.domain.entity.QInventory.inventory
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class InventoryQuery(private val queryFactory: JPAQueryFactory) {

    fun findAllBySkuIdIn(ids: List<String>): List<Inventory> = queryFactory.selectFrom(inventory)
        .where(
            inventory.skuId.`in`(ids),
        )
        .fetch()
}
