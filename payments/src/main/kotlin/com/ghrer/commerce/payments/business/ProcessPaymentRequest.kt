package com.ghrer.commerce.payments.business

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProcessPaymentRequest(
    val orderId: String,
    val customerId: String,
    val customerAddress: Address,
    val totalPrice: Double,
    val items: List<Item>
)

data class Item(
    val id: String,
    val price: Double,
    val quantity: Int
)

data class Address(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val postCode: String,
    val city: String,
)
