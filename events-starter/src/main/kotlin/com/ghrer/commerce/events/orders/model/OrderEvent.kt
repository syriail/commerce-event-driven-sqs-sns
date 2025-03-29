package com.ghrer.commerce.events.orders.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.CommerceEventTopic
import java.time.Instant

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class OrderEventType(private val value: String) {
    ORDER_CREATED("ORDER_CREATED"),
    ORDER_PAYMENT_SUCCESSFUL("ORDER_PAYMENT_SUCCESSFUL"),
    ORDER_PAYMENT_FAILED("ORDER_PAYMENT_FAILED"),
}
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = OrderCreatedEvent::class, name = "ORDER_CREATED"),
    JsonSubTypes.Type(value = OrderPaymentSuccessfulEvent::class, name = "ORDER_PAYMENT_SUCCESSFUL"),
    JsonSubTypes.Type(value = OrderPaymentFailedEvent::class, name = "ORDER_PAYMENT_FAILED"),
)
abstract class OrderEvent(
    open val orderId: String,
    override val topic: CommerceEventTopic = CommerceEventTopic.ORDERS_TOPIC,
    override val timestamp: Instant = Instant.now(),
) : CommerceEvent {
    override val eventGroupId: String
        get() = orderId
}
