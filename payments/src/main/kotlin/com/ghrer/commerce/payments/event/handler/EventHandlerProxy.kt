package com.ghrer.commerce.payments.event.handler

import com.ghrer.commerce.events.CommerceEvent
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class EventHandlerProxy(
    private val handlersList: List<EventHandler>
) {

    private lateinit var handlers: Map<KClass<*>, EventHandler>

    @PostConstruct
    fun init() {
        handlers = handlersList.associateBy { it.getSupportedClass() }
    }

    fun handleEvent(event: CommerceEvent) {
        val handler = handlers[event::class]
        checkNotNull(handler) {
            "No handler found for event ${event::class.simpleName}"
        }
        handler.handleEvent(event)
    }
}
