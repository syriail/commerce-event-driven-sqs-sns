package com.ghrer.commerce.events.orders.model

data class OrderPaymentFailedEvent(
    override val orderId: String,
    override val eventType: String = OrderEventType.ORDER_PAYMENT_FAILED.name,
    val reason: String,
) : OrderEvent(orderId)
