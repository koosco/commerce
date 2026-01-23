package com.koosco.orderservice.order.infra.persist.converter

import com.koosco.orderservice.order.domain.vo.Money
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class MoneyConverter : AttributeConverter<Money, Long> {

    override fun convertToDatabaseColumn(attribute: Money?): Long? = attribute?.amount

    override fun convertToEntityAttribute(dbData: Long?): Money? = dbData?.let { Money(it) }
}
