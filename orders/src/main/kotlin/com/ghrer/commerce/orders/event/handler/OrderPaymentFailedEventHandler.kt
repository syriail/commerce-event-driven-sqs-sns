package com.ghrer.commerce.orders.event.handler

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.orders.model.OrderPaymentFailedEvent
import com.ghrer.commerce.orders.business.OrderHandler
import com.ghrer.commerce.orders.model.OrderStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

@Service
class OrderPaymentFailedEventHandler(
    private val orderHandler: OrderHandler
) : EventHandler {

    private val logger = KotlinLogging.logger { }

    override fun handleEvent(event: CommerceEvent) {
        val paymentFailedEvent = event as OrderPaymentFailedEvent
        logger.info { "Handle OrderPaymentFailedEvent: $event" }
        orderHandler.updateOrderPaymentStatus(
            id = UUID.fromString(paymentFailedEvent.orderId),
            status = OrderStatus.PAYMENT_FAILED
        ).block()
    }

    override fun getSupportedClass(): KClass<OrderPaymentFailedEvent> {
        return OrderPaymentFailedEvent::class
    }
}
