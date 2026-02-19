package com.koosco.inventoryservice.infra.persist

import com.koosco.inventoryservice.domain.entity.Inventory
import com.koosco.inventoryservice.domain.entity.QInventory.inventory
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
