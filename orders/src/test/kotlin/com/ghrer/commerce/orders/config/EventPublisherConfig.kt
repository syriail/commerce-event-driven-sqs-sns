package com.ghrer.commerce.orders.config

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.EventPublisherProxy
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class EventPublisherConfig {

    @Bean
    fun mockEventPublisherProxy() = EventPublisherProxyMock()
}

class EventPublisherProxyMock : EventPublisherProxy {

    private val logger = KotlinLogging.logger { }

    override fun publish(event: CommerceEvent) {
        logger.info { "Mock publishing event $event" }
    }
}
