package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.domain.entity.Product
import org.springframework.data.domain.Page

interface ProductSearchPort {
    fun search(command: GetProductListCommand): Page<Product>
}
