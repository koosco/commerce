package com.koosco.catalogservice.application.port

interface InventoryQueryPort {
    fun getAvailability(skuIds: List<String>): Map<String, Boolean>
}
