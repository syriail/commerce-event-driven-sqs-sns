package com.ghrer.commerce.orders.event.handler

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.orders.model.OrderPaymentSuccessfulEvent
import com.ghrer.commerce.orders.business.OrderHandler
import com.ghrer.commerce.orders.model.OrderStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

@Service
class OrderPaymentSuccessfulEventHandler(
    private val orderHandler: OrderHandler
) : EventHandler {

    private val logger = KotlinLogging.logger { }

    override fun handleEvent(event: CommerceEvent) {
        val paymentSuccessfulEvent = event as OrderPaymentSuccessfulEvent
        logger.info { "Handle OrderPaymentSuccessfulEvent: $event" }
        orderHandler.updateOrderPaymentStatus(
            id = UUID.fromString(paymentSuccessfulEvent.orderId),
            paymentId = paymentSuccessfulEvent.paymentId.toString(),
            status = OrderStatus.PAID
        ).block()
    }

    override fun getSupportedClass(): KClass<OrderPaymentSuccessfulEvent> {
        return OrderPaymentSuccessfulEvent::class
    }
}
