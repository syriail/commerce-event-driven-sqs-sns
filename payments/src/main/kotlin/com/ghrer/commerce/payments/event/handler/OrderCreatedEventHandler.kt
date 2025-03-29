package com.ghrer.commerce.payments.event.handler

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.orders.model.OrderCreatedEvent
import com.ghrer.commerce.payments.business.Address
import com.ghrer.commerce.payments.business.Item
import com.ghrer.commerce.payments.business.PaymentProcessor
import com.ghrer.commerce.payments.business.ProcessPaymentRequest
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class OrderCreatedEventHandler(
    private val paymentProcessor: PaymentProcessor
) : EventHandler {
    override fun handleEvent(event: CommerceEvent) {
        val orderCreatedEvent = event as OrderCreatedEvent
        paymentProcessor.processPayment(
            mapToProcessEventRequest(orderCreatedEvent)
        )
    }

    override fun getSupportedClass(): KClass<OrderCreatedEvent> {
        return OrderCreatedEvent::class
    }

    private fun mapToProcessEventRequest(event: OrderCreatedEvent) = ProcessPaymentRequest(
        orderId = event.orderId,
        customerId = event.customerId,
        customerAddress = with(event.customerAddress) {
            Address(
                firstName,
                lastName,
                street,
                houseNumber,
                postCode,
                city
            )
        },
        totalPrice = event.totalPrice,
        items = event.items.map {
            Item(it.id, it.price, it.quantity)
        }
    )
}
