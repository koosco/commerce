package com.koosco.inventoryservice.infra.persist

import com.koosco.inventoryservice.domain.entity.Inventory
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface JpaInventoryRepository : JpaRepository<Inventory, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.skuId = :skuId")
    fun findByIdWithLock(skuId: String): Inventory?

    /**
     * Pessimistic write lock을 사용하여 재고 조회
     * SELECT ... FOR UPDATE 쿼리 실행
     * 데드락 방지를 위해 skuId 순으로 정렬
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.skuId IN :skuIds ORDER BY i.skuId")
    fun findAllBySkuIdInWithLock(skuIds: List<String>): List<Inventory>

    /**
     * 일반 구매 재고 차감 (조건부 UPDATE)
     * 가용 재고(total_stock - reserved_stock)가 요청 수량 이상일 때만 차감
     * @return 영향받은 row 수 (1: 성공, 0: 재고 부족)
     */
    @Modifying(clearAutomatically = true)
    @Query(
        "UPDATE Inventory i SET i.stock.total = i.stock.total - :quantity " +
            "WHERE i.skuId = :skuId AND (i.stock.total - i.stock.reserved) >= :quantity",
    )
    fun deductQuantity(skuId: String, quantity: Int): Int
}
