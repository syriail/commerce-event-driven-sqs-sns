package com.ghrer.commerce.events.orders.model

import java.util.UUID

data class OrderPaymentSuccessfulEvent(
    override val orderId: String,
    override val eventType: String = OrderEventType.ORDER_PAYMENT_SUCCESSFUL.name,
    val paymentId: UUID,
) : OrderEvent(orderId)
