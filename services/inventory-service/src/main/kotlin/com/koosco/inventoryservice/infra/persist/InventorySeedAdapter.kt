package com.koosco.inventoryservice.infra.persist

import com.koosco.inventoryservice.application.port.InventorySeedPort
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : InventorySeedAdapter
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:57
 * description    : 더미 데이터 영속성 처리를 위한 클래스, local profile only
 */
@Profile("test")
@Component
class InventorySeedAdapter(private val entityManager: EntityManager) : InventorySeedPort {

    @Transactional
    override fun initStock(skuId: String, initialQuantity: Int) {
        entityManager.createQuery(
            """
            UPDATE Inventory i
            SET i.stock.total = :quantity,
                i.stock.reserved = 0
            WHERE i.skuId = :skuId
        """,
        )
            .setParameter("skuId", skuId)
            .setParameter("quantity", initialQuantity)
            .executeUpdate()
    }

    @Transactional
    override fun initStocks(skuIds: List<String>, initialQuantity: Int) {
        skuIds.forEach { skuId -> initStock(skuId, initialQuantity) }
    }

    @Transactional
    override fun clear() {
        entityManager.createQuery("DELETE FROM Inventory").executeUpdate()
    }
}
