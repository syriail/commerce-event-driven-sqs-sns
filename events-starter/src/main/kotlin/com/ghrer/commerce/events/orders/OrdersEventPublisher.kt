package com.ghrer.commerce.events.orders

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.CommerceEventPublisher
import com.ghrer.commerce.events.orders.model.OrderEvent

interface OrdersEventPublisher : CommerceEventPublisher {

    override fun doesSupport(event: CommerceEvent) = event is OrderEvent
}
