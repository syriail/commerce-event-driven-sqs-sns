package com.ghrer.commerce.payments.business

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.EventPublisherProxy
import com.ghrer.commerce.events.orders.model.OrderPaymentFailedEvent
import com.ghrer.commerce.events.orders.model.OrderPaymentSuccessfulEvent
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.capture
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.UUID

class PaymentProcessorTest {

    private val eventPublisherProxy = mock<EventPublisherProxy>()

    private val paymentProcessor = PaymentProcessor(eventPublisherProxy)

    private val eventCaptor = argumentCaptor<CommerceEvent>()

    @Test
    fun `should publish OrderPaymentSuccessfulEvent when customerId does not contain decline`() {
        val request = getSampleProcessPaymentRequest(false)

        assertDoesNotThrow {
            paymentProcessor.processPayment(request)
        }

        verify(eventPublisherProxy).publish(eventCaptor.capture())

        val publishedEvent = eventCaptor.firstValue

        Assertions.assertThat(publishedEvent).isInstanceOf(OrderPaymentSuccessfulEvent::class.java)

        (publishedEvent as OrderPaymentSuccessfulEvent).let {
            Assertions.assertThat(it.orderId).isEqualTo(request.orderId)
            Assertions.assertThat(it.paymentId).isNotNull()
        }
    }

    @Test
    fun `should publish OrderPaymentFailedEvent when customerId contains decline`() {
        val request = getSampleProcessPaymentRequest(true)

        assertDoesNotThrow {
            paymentProcessor.processPayment(request)
        }

        verify(eventPublisherProxy).publish(eventCaptor.capture())

        val publishedEvent = eventCaptor.firstValue

        Assertions.assertThat(publishedEvent).isInstanceOf(OrderPaymentFailedEvent::class.java)

        (publishedEvent as OrderPaymentFailedEvent).let {
            Assertions.assertThat(it.orderId).isEqualTo(request.orderId)
            Assertions.assertThat(it.reason).isNotEmpty()
        }
    }

    private fun getSampleProcessPaymentRequest(shouldDeclined: Boolean) = ProcessPaymentRequest(
        orderId = UUID.randomUUID().toString(),
        customerId = if (shouldDeclined) "decline@me.please" else "accept@me.please",
        customerAddress = Address(
            firstName = "Hussein",
            lastName = "Ghrer",
            street = "am Strand Strasse",
            houseNumber = "5C",
            postCode = "69",
            city = "Neustadt am See"
        ),
        totalPrice = 83.4,
        items = listOf(
            Item(
                id = UUID.randomUUID().toString(),
                price = 4.3,
                quantity = 4
            ),
            Item(
                id = UUID.randomUUID().toString(),
                price = 44.5,
                quantity = 1
            )
        )
    )
}
