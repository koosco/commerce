package com.koosco.orderservice.infra.client

import com.koosco.orderservice.application.port.CatalogQueryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("test")
@Component
class TestCatalogQueryAdapter : CatalogQueryPort {

    override fun getSkuInfos(skuIds: List<Long>): Map<Long, CatalogQueryPort.SkuInfo> {
        // test profile에서는 외부 catalog-service 호출 없이 검증을 통과시킨다.
        return skuIds.associateWith { skuId ->
            CatalogQueryPort.SkuInfo(
                skuPkId = skuId,
                skuId = "SKU-$skuId",
                productId = skuId,
                productName = "테스트 상품 $skuId",
                price = 10000,
                status = "ACTIVE",
            )
        }
    }
}
