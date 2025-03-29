package com.ghrer.commerce.orders.business

import com.ghrer.commerce.events.EventPublisherProxy
import com.ghrer.commerce.events.orders.model.Address
import com.ghrer.commerce.events.orders.model.Item
import com.ghrer.commerce.events.orders.model.OrderCreatedEvent
import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.model.OrderStatus
import com.ghrer.commerce.orders.persistence.OrderPersistenceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class OrderHandler(
    private val orderPersistenceService: OrderPersistenceService,
    private val eventPublisher: EventPublisherProxy,
) {
    fun createOrder(createOrderRequest: CreateOrderRequest): Mono<OrderAggregate> {
        return orderPersistenceService.createOrder(createOrderRequest)
            .doOnNext {
                eventPublisher.publish(
                    mapOrderToOrderCreatedEvent(it)
                )
            }
    }

    fun updateOrderPaymentStatus(id: UUID, status: OrderStatus, paymentId: String? = null): Mono<Void> {
        return orderPersistenceService.updateOrderPaymentStatus(id, status, paymentId).flatMap { Mono.empty() }
    }

    private fun mapOrderToOrderCreatedEvent(order: OrderAggregate) = OrderCreatedEvent(
        orderId = order.id.toString(),
        customerId = order.customerId,
        customerAddress = with(order.customerAddress) {
            Address(
                firstName,
                lastName,
                street,
                houseNumber,
                postCode,
                city,

            )
        },
        createDate = order.createDate,
        status = order.status.name,
        totalPrice = order.totalPrice,
        items = order.items.map {
            Item(it.id.toString(), it.quantity, it.price)
        }
    )
}
