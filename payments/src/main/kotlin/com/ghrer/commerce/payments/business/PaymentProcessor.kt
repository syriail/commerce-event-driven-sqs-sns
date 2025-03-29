package com.ghrer.commerce.payments.business

import com.ghrer.commerce.events.EventPublisherProxy
import com.ghrer.commerce.events.orders.model.OrderPaymentFailedEvent
import com.ghrer.commerce.events.orders.model.OrderPaymentSuccessfulEvent
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PaymentProcessor(
    private val eventPublisher: EventPublisherProxy,
) {

    fun processPayment(processPaymentRequest: ProcessPaymentRequest) {
        if (processPaymentRequest.customerId.contains("decline", ignoreCase = true)) {
            eventPublisher.publish(
                OrderPaymentFailedEvent(
                    orderId = processPaymentRequest.orderId,
                    reason = "Card decline"
                )
            )
        } else {
            eventPublisher.publish(
                OrderPaymentSuccessfulEvent(
                    orderId = processPaymentRequest.orderId,
                    paymentId = UUID.randomUUID()
                )
            )
        }
    }
}
