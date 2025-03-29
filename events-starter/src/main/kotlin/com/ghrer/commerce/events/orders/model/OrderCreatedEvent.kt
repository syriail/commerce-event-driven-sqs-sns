package com.ghrer.commerce.events.orders.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderCreatedEvent(
    override val eventType: String = OrderEventType.ORDER_CREATED.name,
    override val orderId: String,
    val customerId: String,
    val customerAddress: Address,
    val paymentId: String? = null,
    val shipmentId: String? = null,
    val totalPrice: Double,
    val status: String,
    val createDate: LocalDateTime,
    val items: List<Item>
) : OrderEvent(orderId) {
    override val eventGroupId: String
        get() = orderId
}

data class Item(
    val id: String,
    val quantity: Int,
    val price: Double
)

data class Address(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val postCode: String,
    val city: String,
)
