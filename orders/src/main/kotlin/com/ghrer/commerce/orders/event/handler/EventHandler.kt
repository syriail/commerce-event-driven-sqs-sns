package com.ghrer.commerce.orders.event.handler

import com.ghrer.commerce.events.CommerceEvent
import kotlin.reflect.KClass

interface EventHandler {

    fun handleEvent(event: CommerceEvent)

    fun getSupportedClass(): KClass<*>
}
