package com.koosco.orderservice.infra.client

import com.koosco.orderservice.application.port.InventoryReservationPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("test")
@Component
class TestInventoryReservationAdapter : InventoryReservationPort {

    override fun reserve(command: InventoryReservationPort.ReserveCommand) {
        // test profile에서는 외부 inventory-service 호출 없이 예약 성공으로 처리한다.
    }
}
